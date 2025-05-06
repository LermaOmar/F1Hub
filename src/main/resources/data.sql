-- ========== Auctionable Entities: Drivers ==========
INSERT INTO auctionable_entity (price, points, previous_points, active, nationality, type) VALUES
(50000000, 0, 0, TRUE, 'Netherlands', 'Driver'),
(8000000, 0, 0, TRUE, 'New Zealand', 'Driver'),
(30000000, 0, 0, TRUE, 'Monaco', 'Driver'),
(20000000, 0, 0, TRUE, 'United Kingdom', 'Driver'),
(40000000, 0, 0, TRUE, 'United Kingdom', 'Driver'),
(30000000, 0, 0, TRUE, 'Australia', 'Driver'),
(30000000, 0, 0, TRUE, 'United Kingdom', 'Driver'),
(12000000, 0, 0, TRUE, 'Italy', 'Driver'),
(11000000, 0, 0, TRUE, 'Spain', 'Driver'),
(7000000, 0, 0, TRUE, 'Canada', 'Driver'),
(9000000, 0, 0, TRUE, 'France', 'Driver'),
(800000, 0, 0, TRUE, 'Australia', 'Driver'),
(1500000, 0, 0, TRUE, 'France', 'Driver'),
(1200000, 0, 0, TRUE, 'United Kingdom', 'Driver'),
(3500000, 0, 0, TRUE, 'Brazil', 'Driver'),
(10000000, 0, 0, TRUE, 'Thailand', 'Driver'),
(15000000, 0, 0, TRUE, 'Spain', 'Driver'),
(10000000, 0, 0, TRUE, 'Japan', 'Driver'),
(750000, 0, 0, TRUE, 'France', 'Driver'),
(1500000, 0, 0, TRUE, 'Germany', 'Driver');

INSERT INTO driver (id, name) VALUES
(1, 'Max Verstappen'),
(2, 'Liam Lawson'),
(3, 'Charles Leclerc'),
(4, 'Lewis Hamilton'),
(5, 'Lando Norris'),
(6, 'Oscar Piastri'),
(7, 'George Russell'),
(8, 'Andrea Kimi Antonelli'),
(9, 'Fernando Alonso'),
(10, 'Lance Stroll'),
(11, 'Pierre Gasly'),
(12, 'Jack Doohan'),
(13, 'Esteban Ocon'),
(14, 'Oliver Bearman'),
(15, 'Gabriel Bortoleto'),
(16, 'Alex Albon'),
(17, 'Carlos Sainz'),
(18, 'Yuki Tsunoda'),
(19, 'Isack Hadjar'),
(20, 'Nico Hülkenberg');

-- ========== Auctionable Entities: Teams ==========
INSERT INTO auctionable_entity (price, points, previous_points, active, nationality, type) VALUES
(50000000, 0, 0, TRUE, 'Austria', 'Team'),
(35000000, 0, 0, TRUE, 'Germany', 'Team'),
(35000000, 0, 0, TRUE, 'Italy', 'Team'),
(60000000, 0, 0, TRUE, 'United Kingdom', 'Team'),
(10000000, 0, 0, TRUE, 'United Kingdom', 'Team'),
(8000000, 0, 0, TRUE, 'France', 'Team'),
(10000000, 0, 0, TRUE, 'United Kingdom', 'Team'),
(8500000, 0, 0, TRUE, 'Italy', 'Team'),
(5000000, 0, 0, TRUE, 'Switzerland', 'Team'),
(7500000, 0, 0, TRUE, 'United States', 'Team');

INSERT INTO team (id, name) VALUES
(21, 'Red Bull Racing'),
(22, 'Mercedes-AMG Petronas'),
(23, 'Scuderia Ferrari'),
(24, 'McLaren F1 Team'),
(25, 'Aston Martin Aramco F1 Team'),
(26, 'Alpine F1 Team'),
(27, 'Williams Racing'),
(28, 'Visa Cash App RB F1 Team'),
(29, 'Stake F1 Team Kick Sauber'),
(30, 'Haas F1 Team');

-- ========== League ==========
INSERT INTO league (name) VALUES
('F1 League');

-- ========== Market ==========
INSERT INTO market (league_id) VALUES
(1);

-- ========== Market Items ==========
INSERT INTO market_item (auctionable_entity_id, available) VALUES
(1, FALSE),
(2, FALSE),
(3, FALSE),
(4, FALSE),
(5, FALSE),
(6, FALSE),
(7, FALSE),
(8, FALSE),
(9, FALSE),
(10, FALSE),
(11, FALSE),
(12, FALSE),
(13, FALSE),
(14, FALSE),
(15, FALSE),
(16, FALSE),
(17, FALSE),
(18, FALSE),
(19, FALSE),
(20, FALSE),
(21, FALSE),
(22, FALSE),
(23, FALSE),
(24, FALSE),
(25, FALSE),
(26, FALSE),
(27, FALSE),
(28, FALSE),
(29, FALSE),
(30, FALSE);

-- ========== MarketItem-Markets (Many-to-Many Relation) ==========
INSERT INTO market_item_markets (market_item_id, markets_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1),
(11, 1),
(12, 1),
(13, 1),
(14, 1),
(15, 1),
(16, 1),
(17, 1),
(18, 1),
(19, 1),
(20, 1),
(21, 1),
(22, 1),
(23, 1),
(24, 1),
(25, 1),
(26, 1),
(27, 1),
(28, 1),
(29, 1),
(30, 1);
