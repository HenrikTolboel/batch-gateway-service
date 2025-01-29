SET NAMES utf8mb4;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';


-- TABLES:

CREATE TABLE `DomainEvents` (
                                `id` INT NOT NULL AUTO_INCREMENT,
                                `event_id` varchar(64) NOT NULL,
                                `seconds` BIGINT NOT NULL,
                                `nanos` BIGINT NOT NULL,
                                `attributes` JSON NOT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE `user_id_unique` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `KeysTable` (
                                `id` INT NOT NULL AUTO_INCREMENT,
                                `key_id` varchar(64) NOT NULL,
                                `earliest_action_needed_seconds` BIGINT NOT NULL,
                                `attributes` JSON NOT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE `key_id_unique` (`key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `Users` (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `user_id` varchar(64) NOT NULL,
                          `name` varchar(64) NOT NULL,
                          `tenant_id` varchar(64) NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE `user_id_unique` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

