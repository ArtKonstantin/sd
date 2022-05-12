INSERT INTO users (login, password, role)
VALUES ('admin', '$2a$10$M/mSPU0lh3fgvX0QMgczw.aPfJibyxsGt4IPlQWLkdgIrMSW0FC3q', 'ADMIN');

INSERT INTO sla (criticality, reaction_time, lead_time)
VALUES ('HIGH', '00:10', '01:00'),
       ('AVERAGE', '00:30', '03:00'),
       ('LOW', '01:00', '06:00');