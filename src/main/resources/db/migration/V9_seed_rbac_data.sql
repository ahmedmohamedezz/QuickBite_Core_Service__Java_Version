INSERT INTO roles (name, display_name, description, created_at, updated_at)
VALUES ('owner', 'Restaurant Owner', 'Full access to all restaurant resources', NOW(), NOW()),
       ('branch_manager', 'Branch Manager', 'Manages branch operations and staff', NOW(), NOW()),
       ('staff', 'Staff Member', 'Limited access for daily operations', NOW(),
        NOW()) ON CONFLICT (name) DO NOTHING;

-- Insert permissions with AWS-like resource naming
INSERT INTO permissions (resource, action, created_at)
VALUES
-- Product permissions
('core:product', 'create', NOW()),
('core:product', 'read', NOW()),
('core:product', 'update', NOW()),

-- Member permissions
('core:member', 'create', NOW()),
('core:member', 'read', NOW()),
('core:member', 'update', NOW()),
('core:member', 'delete', NOW()),

-- Branch permissions
('core:branch', 'create', NOW()),
('core:branch', 'update', NOW()),

-- Restaurant settings permissions
('core:restaurant', 'update', NOW()) ON CONFLICT (resource, action) DO NOTHING;

-- Owner gets ALL permissions
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, NOW()
FROM roles r,
     permissions p
WHERE r.name = 'owner' ON CONFLICT DO NOTHING;

-- Branch Manager permissions - product CRUD, member read, branch update
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, NOW()
FROM roles r,
     permissions p
WHERE r.name = 'branch_manager'
  AND p.resource || ':' || p.action IN (
                                        'core:product:create',
                                        'core:product:read',
                                        'core:product:update',
                                        'core:member:read',
                                        'core:branch:update'
    )

    ON CONFLICT DO NOTHING;

-- Staff permissions - read only
INSERT INTO role_permissions (role_id, permission_id, created_at)
SELECT r.id, p.id, NOW()
FROM roles r,
     permissions p
WHERE r.name = 'staff'
  AND p.resource || ':' || p.action IN
      ('core:product:read', 'core:member:read') ON CONFLICT DO NOTHING;
