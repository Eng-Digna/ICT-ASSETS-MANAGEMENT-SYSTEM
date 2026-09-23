INSERT INTO department (name) VALUES
    ('ICT'),
    ('Finance'),
    ('Operations'),
    ('Human Resources'),
    ('Procurement'),
    ('Corporate Affairs'),
    ('Administration')
ON CONFLICT (name) DO NOTHING;

INSERT INTO station (name) VALUES
    ('Dar es Salaam HQ'),
    ('Tanga Port'),
    ('Mtwara Port'),
    ('Zanzibar Port'),
    ('Kigoma Port'),
    ('Mwanza Port')
ON CONFLICT (name) DO NOTHING;
