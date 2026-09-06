package com.quickbite.core.rbac.service;

import com.quickbite.core.auth.dto.UserRegisterDto;
import com.quickbite.core.auth.exception.UserAlreadyExistsException;
import com.quickbite.core.auth.service.AuthService;
import com.quickbite.core.auth.service.PasswordResetService;
import com.quickbite.core.auth.utils.AuthUtils;
import com.quickbite.core.branch.exception.BranchNotFoundException;
import com.quickbite.core.branch.service.BranchService;
import com.quickbite.core.common.mail.EmailService;
import com.quickbite.core.rbac.domain.MemberBranchEntity;
import com.quickbite.core.rbac.domain.BranchMemberId;
import com.quickbite.core.rbac.domain.RestaurantMemberEntity;
import com.quickbite.core.rbac.domain.RoleEntity;
import com.quickbite.core.rbac.dto.MemberWithRoleNameProjection;
import com.quickbite.core.rbac.dto.RestaurantMemberCreateDto;
import com.quickbite.core.rbac.dto.RestaurantMemberDto;
import com.quickbite.core.rbac.dto.RoleOrStatusDto;
import com.quickbite.core.rbac.enums.MemberStatus;
import com.quickbite.core.rbac.exception.CannotAssignBranchesToOwnerException;
import com.quickbite.core.rbac.exception.CannotCreateOwnerUserException;
import com.quickbite.core.rbac.exception.MemberNotFoundException;
import com.quickbite.core.rbac.exception.RoleNotFoundException;
import com.quickbite.core.rbac.mapper.RestaurantMemberMapper;
import com.quickbite.core.rbac.repository.MemberBranchRepository;
import com.quickbite.core.rbac.repository.RestaurantMemberRepository;
import com.quickbite.core.rbac.repository.RoleRepository;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import com.quickbite.core.restaurant.exception.RestaurantNotFoundException;
import com.quickbite.core.restaurant.service.RestaurantService;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final RoleRepository roleRepository;

    private final MemberBranchRepository memberBranchRepository;

    private final RestaurantMemberRepository restaurantMemberRepository;

    private final AuthUtils authUtils;

    private final PasswordResetService passwordResetService;

    private final RestaurantService restaurantService;

    private final UserService userService;

    private final BranchService branchService;

    private final EmailService emailService;

    private final RestaurantMemberMapper restaurantMemberMapper;

    @Transactional
    public RestaurantMemberDto createMember(Long restaurantId, RestaurantMemberCreateDto dto) {
        // restaurant can't have more than 1 owner
        if (dto.role().equals("owner")) {
            throw new CannotCreateOwnerUserException();
        }

        // check restaurant exists
        if (!restaurantService.existsById(restaurantId)) {
            throw new RestaurantNotFoundException();
        }

        // check user exists
        if (userService.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException();
        }

        // find role
        RoleEntity role = roleRepository.findFirstByName(dto.role())
                .orElseThrow(RoleNotFoundException::new);


        // create user, member, assign branches
        UserRegisterDto user = new UserRegisterDto(
                dto.name(), dto.phoneNumber(),
                dto.email(), "DummyPassword@123",
                SystemRole.restaurant_user
        );

        UserDto savedUser = userService.create(user);

        RestaurantMemberEntity restaurantMember = RestaurantMemberEntity.builder()
                .restaurant(restaurantService.getProxy(restaurantId))
                .user(userService.getUserProxy(savedUser.id()))
                .role(role)
                .status(MemberStatus.inactive)
                .build();

        RestaurantMemberEntity savedRestaurantMember =
                restaurantMemberRepository.save(restaurantMember);

        // check branches belong to restaurant
        if (!branchService.checkBranchesExistInRestaurant(restaurantId, new HashSet<>(dto.branchIds()))) {
            throw new BranchNotFoundException();
        }

        List<MemberBranchEntity> memberBranchEntities = dto.branchIds()
                .stream().map(branchId ->
                        MemberBranchEntity.builder()
                                .id(
                                        // composite PK
                                        BranchMemberId.builder()
                                                .branchId(branchId)
                                                .memberId(savedRestaurantMember.getId())
                                                .build()
                                )
                                .branch(branchService.getBranchProxy(branchId))
                                .member(savedRestaurantMember)
                                .build()
                ).toList();

        memberBranchRepository.setMemberBranches(savedRestaurantMember.getId(), memberBranchEntities);

        // generate OTP, create reset password record, and send mail
        String otp = authUtils.generateOTP();
        String otpHash = authUtils.hashOTP(otp);

        passwordResetService.createPasswordResetRecord(savedUser.id(), otpHash,
                LocalDateTime.now().plusHours(1));
        emailService.mockEmail("Mock Mail OTP: ", otp);

        return restaurantMemberMapper.toDto(savedRestaurantMember);
    }

    @Transactional
    public Map<String, Object> updateMemberBranches(Long restaurantId,
                                                    Long memberId, List<Long> branchIds) {

        RestaurantDto restaurant = restaurantService.findById(restaurantId);
        RestaurantMemberEntity memberEntity = restaurantMemberRepository.findById(memberId).orElseThrow(
                MemberNotFoundException::new
        );

        // check restaurant exists
        if (restaurant == null) {
            throw new RestaurantNotFoundException();
        }

        // check this member is not owner (owners have access to all branches by default)
        if (restaurant.ownerId().equals(memberEntity.getUser().getId())) {
            throw new CannotAssignBranchesToOwnerException();
        }

        // check Ids belong to this restaurant
        if (!branchService.checkBranchesExistInRestaurant(restaurantId, new HashSet<>(branchIds))) {
            throw new BranchNotFoundException();
        }

        List<MemberBranchEntity> branchEntities = branchIds.stream().map(branchId -> {
            BranchMemberId pk = BranchMemberId.builder()
                    .branchId(branchId)
                    .memberId(memberId)
                    .build();

            return memberBranchRepository.getReferenceById(pk);
        }).toList();

        memberBranchRepository.setMemberBranches(memberId, branchEntities);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Member branches updated");
        result.put("branchIds", branchIds);

        return result;
    }

    @Transactional
    public void activateMemberByUserId(Long userId) {
        restaurantMemberRepository.activateMemberByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<MemberWithRoleNameProjection> findMemberWithRoleName(Long userId) {
        return restaurantMemberRepository.findMemberWithRoleName(userId);
    }

    @Transactional(readOnly = true)
    public List<Long> findBranchIdsByMemberId(Long memberId) {
        return memberBranchRepository.findBranchIdsByMemberId(memberId);
    }

    @Transactional
    public void createOwnerMember(Long restaurantId, Long userId) {
        RoleEntity ownerRole =
                roleRepository.findFirstByName("owner").orElseThrow(RoleNotFoundException::new);

        RestaurantMemberEntity ownerMember = RestaurantMemberEntity.builder()
                .restaurant(restaurantService.getProxy(restaurantId))
                .user(userService.getUserProxy(userId))
                .role(ownerRole)
                .status(MemberStatus.active)
                .build();

        restaurantMemberRepository.save(ownerMember);
    }

    @Transactional(readOnly = true)
    public List<RestaurantMemberDto> getRestaurantMembers(Long restaurantId) {
        List<RestaurantMemberEntity> restaurantMembers =
                restaurantMemberRepository.findByRestaurantId(restaurantId);

        if (restaurantMembers == null || restaurantMembers.isEmpty()) {
            return new ArrayList<>();
        }

        return restaurantMembers.stream().map(restaurantMemberMapper::toDto).toList();
    }

    @Transactional
    public Map<String, Object> updateMember(Long restaurantId, Long memberId, RoleOrStatusDto body) {
        RestaurantMemberEntity member =
                restaurantMemberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        if (!member.getRestaurant().getId().equals(restaurantId)) {
            throw new MemberNotFoundException();
        }

        if (body.status() != null) {
            member.setStatus(body.status());
        }

        if (body.role() != null) {
            if (body.role().equals("owner")) {
                throw new CannotCreateOwnerUserException();
            }

            RoleEntity role =
                    roleRepository.findFirstByName(body.role()).orElseThrow(RoleNotFoundException::new);

            member.setRole(role);
        }

        return Map.of("message", "member updated successfully");
    }

    @Transactional
    public Map<String, Object> deleteMember(Long restaurantId, Long memberId) {
        restaurantMemberRepository.deleteByIdAndRestaurantId(memberId, restaurantId);
        return Map.of("message", "member deleted successfully");
    }
}
