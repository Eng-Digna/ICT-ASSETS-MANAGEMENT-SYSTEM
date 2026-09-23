INSERT INTO asset (asset_type, serial_number, mac_address, brand, model, operating_system, warranty_start_date, warranty_end_date, status, department_id, station_id, version)
SELECT 'LAPTOP', 'LPT-TPA-001', '00:1B:44:11:3A:B7', 'Dell', 'Latitude 7420', 'Windows 11 Pro', '2023-01-15', '2026-01-15', 'REGISTERED', 
(SELECT id FROM department WHERE name = 'ICT'), (SELECT id FROM station WHERE name = 'Dar es Salaam HQ'), 0
WHERE NOT EXISTS (SELECT 1 FROM asset WHERE serial_number = 'LPT-TPA-001');

INSERT INTO asset (asset_type, serial_number, mac_address, brand, model, operating_system, warranty_start_date, warranty_end_date, status, department_id, station_id, version)
SELECT 'DESKTOP', 'DSK-TPA-002', '00:1B:44:22:3B:C8', 'HP', 'EliteDesk 800 G6', 'Windows 10 Pro', '2022-05-10', '2025-05-10', 'REGISTERED', 
(SELECT id FROM department WHERE name = 'Finance'), (SELECT id FROM station WHERE name = 'Tanga Port'), 0
WHERE NOT EXISTS (SELECT 1 FROM asset WHERE serial_number = 'DSK-TPA-002');

INSERT INTO asset (asset_type, serial_number, mac_address, brand, model, operating_system, warranty_start_date, warranty_end_date, status, department_id, station_id, version)
SELECT 'PRINTER', 'PRN-TPA-003', '00:1B:44:33:4C:D9', 'Kyocera', 'ECOSYS M5526cdw', NULL, '2024-02-20', '2025-02-20', 'REGISTERED', 
(SELECT id FROM department WHERE name = 'Operations'), (SELECT id FROM station WHERE name = 'Mtwara Port'), 0
WHERE NOT EXISTS (SELECT 1 FROM asset WHERE serial_number = 'PRN-TPA-003');

INSERT INTO asset (asset_type, serial_number, mac_address, brand, model, operating_system, warranty_start_date, warranty_end_date, status, department_id, station_id, version)
SELECT 'SCANNER', 'SCN-TPA-004', NULL, 'Epson', 'WorkForce DS-730N', NULL, '2023-11-05', '2024-11-05', 'REGISTERED', 
(SELECT id FROM department WHERE name = 'Human Resources'), (SELECT id FROM station WHERE name = 'Dar es Salaam HQ'), 0
WHERE NOT EXISTS (SELECT 1 FROM asset WHERE serial_number = 'SCN-TPA-004');

INSERT INTO asset (asset_type, serial_number, mac_address, brand, model, operating_system, warranty_start_date, warranty_end_date, status, department_id, station_id, version)
SELECT 'LAPTOP', 'LPT-TPA-005', '00:1B:44:55:6E:FA', 'Lenovo', 'ThinkPad T14', 'Windows 11 Pro', '2024-01-10', '2027-01-10', 'REGISTERED', 
(SELECT id FROM department WHERE name = 'Procurement'), (SELECT id FROM station WHERE name = 'Mwanza Port'), 0
WHERE NOT EXISTS (SELECT 1 FROM asset WHERE serial_number = 'LPT-TPA-005');
