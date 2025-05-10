-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 03, 2025 at 08:48 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `checkers`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `total_games` int(11) DEFAULT 0,
  `wins` int(11) DEFAULT 0,
  `losses` int(11) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `password`, `email`, `total_games`, `wins`, `losses`, `created_at`) VALUES
('hi', 'hi', 'hi', 2, 2, 0, '2025-05-03 06:04:06'),
('omarsoussi', 'Omar@123', 'omar@gmail.com', 2, 2, 0, '2025-05-02 22:35:55'),
('yassin', 'Yassin123', 'yassin@gmail.com', 2, 2, 0, '2025-05-03 04:45:59');

-- --------------------------------------------------------

--
-- Table structure for table `user_difficulty_stats`
--

CREATE TABLE `user_difficulty_stats` (
  `username` varchar(50) NOT NULL,
  `difficulty` enum('EASY','MEDIUM','HARD') NOT NULL,
  `game_count` int(11) DEFAULT 0
) ;

--
-- Dumping data for table `user_difficulty_stats`
--

INSERT INTO `user_difficulty_stats` (`username`, `difficulty`, `game_count`) VALUES
('hi', 'EASY', 2),
('hi', 'MEDIUM', 0),
('hi', 'HARD', 0),
('omarsoussi', 'EASY', 2),
('omarsoussi', 'MEDIUM', 0),
('omarsoussi', 'HARD', 0),
('yassin', 'EASY', 2),
('yassin', 'MEDIUM', 0),
('yassin', 'HARD', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_users_email` (`email`);

--
-- Indexes for table `user_difficulty_stats`
--
ALTER TABLE `user_difficulty_stats`
  ADD PRIMARY KEY (`username`,`difficulty`),
  ADD KEY `idx_difficulty_stats` (`username`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_difficulty_stats`
--
ALTER TABLE `user_difficulty_stats`
  ADD CONSTRAINT `user_difficulty_stats_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
