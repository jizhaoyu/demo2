-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: book_trading
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '书籍ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '书名',
  `author` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作者',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `condition_level` tinyint NOT NULL COMMENT '成色：1-全新，2-九成新，3-八成新，4-七成新，5-六成新及以下',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `images` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片URL（JSON数组）',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-在售，2-已预订，3-已售，4-下架',
  `user_id` bigint NOT NULL COMMENT '卖家ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_price` (`price`),
  CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_book_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书籍表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES (1,'活着','余华',18.00,45.00,2,'余华经典作品，九成新，无笔记无划痕，值得收藏。','https://img0.baidu.com/it/u=1148581691,2698676312&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=1067',3,2,1,157,'2026-01-09 05:04:29','2026-01-09 12:25:21',0),(2,'百年孤独','余华',35.00,68.00,2,'诺贝尔文学奖作品，精装版，品相很好。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,2,1,91,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(3,'红楼梦','余华',25.00,59.00,3,'人民文学出版社版本，八成新，有少量笔记。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,3,1,234,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(4,'围城','余华',15.00,39.00,2,'经典讽刺小说，九成新，推荐阅读。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,4,1,68,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(5,'平凡的世界','余华',45.00,108.00,1,'全新未拆封，三册全套，茅盾文学奖作品。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,5,1,314,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(6,'高等数学（第七版）上册','余华',20.00,48.00,3,'考研必备教材，有部分笔记和习题答案。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,2,2,574,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(7,'线性代数','余华',12.00,28.00,3,'八成新，有课堂笔记，适合考研复习。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,3,2,423,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(8,'数据结构（C语言版）','余华',22.00,49.00,2,'计算机专业必修教材，九成新，无笔记。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,4,2,345,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(9,'计算机网络（第7版）','余华',28.00,59.00,2,'经典教材，九成新，附带课后习题答案。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,5,2,289,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(10,'操作系统概念','余华',55.00,129.00,1,'全新，英文原版，适合深入学习操作系统。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,2,2,178,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(11,'深入理解计算机系统','余华',68.00,139.00,2,'CSAPP经典，程序员必读，九成新。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,3,3,456,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(12,'算法导论','余华',75.00,168.00,2,'算法圣经，第三版，品相良好。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,4,3,389,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(13,'JavaScript高级程序设计','余华',45.00,129.00,2,'红宝书第四版，前端必备，九成新。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,5,3,267,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(14,'Python编程：从入门到实践','余华',35.00,89.00,1,'全新，适合Python初学者。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,2,3,534,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(15,'代码整洁之道','余华',38.00,79.00,2,'程序员必读，教你写出优雅代码。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,3,3,198,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(16,'艺术的故事','余华',85.00,188.00,2,'艺术史入门经典，精装大开本，九成新。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,4,4,146,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(17,'写给大家看的设计书','余华',28.00,59.00,2,'设计入门必读，通俗易懂。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,5,4,234,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(18,'配色设计原理','余华',32.00,68.00,1,'全新，日本设计师经典配色指南。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,2,4,167,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(19,'断舍离','余华',18.00,45.00,2,'整理收纳经典，九成新，改变生活方式。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,3,5,289,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(20,'小家越住越大','余华',25.00,59.00,2,'家居收纳指南，实用性强。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,4,5,182,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(21,'好好吃饭','余华',22.00,48.00,1,'全新，美食家蔡澜的饮食哲学。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,5,5,135,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(22,'三体','余华',55.00,128.00,1,'全新三册套装，科幻巨作。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',3,2,1,678,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(23,'人类简史','余华',42.00,98.00,2,'九成新，畅销历史读物。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',3,3,3,456,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(24,'时间简史','余华',28.00,58.00,3,'八成新，科普经典。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',4,4,3,234,'2026-01-09 05:04:29','2026-01-09 12:39:46',0),(25,'Java核心技术 卷I','余华',58.00,149.00,2,'管理员发布的测试书籍，Java经典教材，九成新。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,1,2,123,'2026-01-09 08:00:15','2026-01-09 12:39:46',0),(26,'Spring实战（第5版）','余华',48.00,109.00,2,'Spring框架权威指南，九成新，适合Java开发者。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,1,3,89,'2026-01-09 08:00:15','2026-01-09 12:39:46',0),(27,'Effective Java（第3版）','余华',65.00,129.00,1,'全新，Java编程最佳实践，程序员必读。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,1,3,156,'2026-01-09 08:00:15','2026-01-09 12:39:46',0),(28,'Java核心技术 卷I','余华',58.00,149.00,2,'管理员发布的测试书籍，Java经典教材，九成新。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,1,2,131,'2026-01-09 08:00:21','2026-01-09 12:39:46',0),(29,'Spring实战（第5版）','余华',48.00,109.00,2,'Spring框架权威指南，九成新，适合Java开发者。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,1,3,89,'2026-01-09 08:00:21','2026-01-09 12:39:46',0),(30,'Effective Java（第3版）','余华',65.00,129.00,1,'全新，Java编程最佳实践，程序员必读。','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,1,3,168,'2026-01-09 08:00:21','2026-01-09 12:39:46',0),(31,'111','余华',11.00,15.00,1,'111','https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',1,1,2,7,'2026-01-09 16:01:28','2026-01-09 12:39:46',1);
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书籍分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'文学',1,'2026-01-08 15:32:32','2026-01-08 15:32:32',0),(2,'教材',2,'2026-01-08 15:32:32','2026-01-08 15:32:32',0),(3,'科技',3,'2026-01-08 15:32:32','2026-01-08 15:32:32',0),(4,'艺术',4,'2026-01-08 15:32:32','2026-01-08 15:32:32',0),(5,'生活',5,'2026-01-08 15:32:32','2026-01-08 15:32:32',0),(6,'其他',6,'2026-01-08 15:32:32','2026-01-08 15:32:32',0);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '书籍ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`,`book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  CONSTRAINT `fk_favorite_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,2,3,'2026-01-09 05:04:30'),(2,2,4,'2026-01-09 05:04:30'),(3,2,11,'2026-01-09 05:04:30'),(4,2,12,'2026-01-09 05:04:30'),(5,3,1,'2026-01-09 05:04:30'),(6,3,2,'2026-01-09 05:04:30'),(7,3,5,'2026-01-09 05:04:30'),(8,3,13,'2026-01-09 05:04:30'),(9,4,6,'2026-01-09 05:04:30'),(10,4,7,'2026-01-09 05:04:30'),(11,4,8,'2026-01-09 05:04:30'),(12,4,14,'2026-01-09 05:04:30'),(13,5,9,'2026-01-09 05:04:30'),(14,5,10,'2026-01-09 05:04:30'),(15,5,15,'2026-01-09 05:04:30'),(16,5,16,'2026-01-09 05:04:30'),(17,6,1,'2026-01-09 05:04:30'),(18,6,11,'2026-01-09 05:04:30'),(19,6,17,'2026-01-09 05:04:30'),(20,6,18,'2026-01-09 05:04:30'),(22,1,20,'2026-01-09 15:39:06');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted_by_sender` tinyint DEFAULT '0' COMMENT '发送者是否删除：0-否，1-是',
  `deleted_by_receiver` tinyint DEFAULT '0' COMMENT '接收者是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_read` (`is_read`),
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,2,3,'你好，请问《活着》这本书还在吗？',1,'2026-01-09 05:04:30',0,0),(2,3,2,'在的，品相很好，有兴趣可以下单。',1,'2026-01-09 05:04:30',0,0),(3,2,3,'好的，我看看，价格能优惠点吗？',1,'2026-01-09 05:04:30',0,0),(4,3,2,'已经是最低价了，包邮哦。',1,'2026-01-09 05:04:30',0,0),(5,2,3,'行，那我下单了。',1,'2026-01-09 05:04:30',0,0),(6,4,5,'请问《算法导论》有课后习题答案吗？',1,'2026-01-09 05:04:30',0,0),(7,5,4,'有的，书里夹着一份打印的答案。',1,'2026-01-09 05:04:30',0,0),(8,4,5,'太好了，我想买这本书。',1,'2026-01-09 05:04:30',0,0),(9,6,2,'《高等数学》笔记多吗？会影响阅读吗？',1,'2026-01-09 05:04:30',0,0),(10,2,6,'笔记主要在重点章节，不影响阅读，反而有助于理解。',1,'2026-01-09 05:04:30',0,0),(11,6,2,'好的，谢谢，我考虑一下。',1,'2026-01-09 05:04:30',0,0),(12,3,4,'你好，《数据结构》这本书能便宜点吗？',1,'2026-01-09 05:04:30',0,0),(13,4,3,'可以的，20块包邮怎么样？',1,'2026-01-09 05:04:30',0,0),(14,3,4,'成交！我这就下单。',0,'2026-01-09 05:04:30',0,0),(15,1,2,'欢迎加入二手书交易平台，祝您交易愉快！',1,'2026-01-09 05:04:30',0,0),(16,1,3,'欢迎加入二手书交易平台，祝您交易愉快！',1,'2026-01-09 05:04:30',0,0),(17,1,4,'欢迎加入二手书交易平台，祝您交易愉快！',1,'2026-01-09 05:04:30',0,0),(18,1,5,'欢迎加入二手书交易平台，祝您交易愉快！',1,'2026-01-09 05:04:30',0,0),(19,1,6,'欢迎加入二手书交易平台，祝您交易愉快！',1,'2026-01-09 05:04:30',0,0),(20,1,2,'你好',0,'2026-01-09 16:37:43',0,0),(21,1,5,'你好',1,'2026-01-09 16:38:02',0,0),(22,5,4,'你好',0,'2026-01-09 16:39:52',0,0),(23,5,1,'你好',1,'2026-01-09 16:40:04',0,0),(24,1,5,'[BOOK:21|好好吃饭|22|https://img3.doubanio.com/view/subject/l/public/s29634663.jpg]',1,'2026-01-09 20:06:40',0,0),(25,5,1,'[BOOK:30|Effective Java（第3版）|65|https://ts1.tc.mm.bing.net/th/id/OIP-C.IJZgTNx1vp9EML_1wV5p2gHaEo?w=260&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2]',0,'2026-01-09 20:52:23',0,0);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `book_id` bigint NOT NULL COMMENT '书籍ID',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `price` decimal(10,2) NOT NULL COMMENT '成交价格',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-待确认，2-已确认，3-已支付，4-已发货，5-已完成，6-已取消',
  `shipping_address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货地址',
  `shipping_info` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '物流信息',
  `cancel_reason` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'ORD20250101000001',22,3,2,55.00,5,'北京市海淀区中关村大街1号 张三 13800000001','顺丰快递 SF1234567890',NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(2,'ORD20250101000002',23,4,3,42.00,5,'上海市浦东新区陆家嘴环路1000号 李四 13800000002','中通快递 ZT9876543210',NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(3,'ORD20250105000001',1,5,2,18.00,5,'广州市天河区天河路385号 王五 13800000003','韵达快递 YD1122334455',NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(4,'ORD20250106000001',6,6,2,20.00,3,'深圳市南山区科技园南区 赵六 13800000004',NULL,NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(5,'ORD20250107000001',11,2,3,68.00,6,'杭州市西湖区文三路269号 张三 13800000001',NULL,NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(6,'ORD20250108000001',16,3,4,85.00,1,'成都市武侯区天府大道北段1700号 李四 13800000002',NULL,NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(7,'ORD20250108000002',12,5,4,75.00,1,'武汉市洪山区珞喻路1037号 王五 13800000003',NULL,NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(8,'ORD20250102000001',24,6,4,28.00,6,'南京市鼓楼区汉口路22号 赵六 13800000004',NULL,NULL,'2026-01-09 05:04:30','2026-01-09 05:04:30',0),(9,'2009596739412586496',21,1,5,22.00,6,'济南市槐荫区',NULL,'滚','2026-01-09 20:02:54','2026-01-09 20:02:54',0);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `reviewer_id` bigint NOT NULL COMMENT '评价者ID',
  `reviewee_id` bigint NOT NULL COMMENT '被评价者ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_reviewer` (`order_id`,`reviewer_id`),
  KEY `idx_reviewee_id` (`reviewee_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_review_reviewee` FOREIGN KEY (`reviewee_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_review_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,1,3,2,5,'书籍品相很好，发货速度快，卖家服务态度很好，推荐！','2026-01-09 05:04:30'),(2,1,2,3,5,'买家很爽快，沟通顺畅，好评！','2026-01-09 05:04:30'),(3,2,4,3,4,'书籍质量不错，物流稍慢，整体满意。','2026-01-09 05:04:30'),(4,2,3,4,5,'买家很有礼貌，交易愉快。','2026-01-09 05:04:30'),(5,3,5,2,5,'太胖\n','2026-01-09 20:42:04');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` tinyint DEFAULT '0' COMMENT '角色：0-普通用户，1-管理员',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `avg_rating` decimal(2,1) DEFAULT '0.0' COMMENT '平均评分',
  `rating_count` int DEFAULT '0' COMMENT '评价数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','admin@booktrading.com','13800000000',NULL,1,1,0.0,0,'2026-01-08 15:32:33','2026-01-09 07:34:17',0),(2,'testuser1','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','test1@booktrading.com','13800000001',NULL,0,1,5.0,2,'2026-01-08 15:32:33','2026-01-09 07:34:17',0),(3,'testuser2','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','test2@booktrading.com','13800000002',NULL,0,1,0.0,0,'2026-01-08 15:32:33','2026-01-09 07:34:17',0),(4,'张三','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','zhangsan@booktrading.com','13800000001','https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan',0,1,4.8,5,'2026-01-09 05:04:29','2026-01-09 07:34:17',0),(5,'李四','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','lisi@booktrading.com','13800000002','https://api.dicebear.com/7.x/avataaars/svg?seed=lisi',0,1,4.5,3,'2026-01-09 05:04:29','2026-01-09 07:34:17',0),(6,'王五','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','wangwu@booktrading.com','13800000003','https://api.dicebear.com/7.x/avataaars/svg?seed=wangwu',0,1,4.2,2,'2026-01-09 05:04:29','2026-01-09 07:34:17',0),(7,'赵六','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','zhaoliu@booktrading.com','13800000004','https://api.dicebear.com/7.x/avataaars/svg?seed=zhaoliu',0,1,4.9,8,'2026-01-09 05:04:29','2026-01-09 07:34:17',0),(8,'小明','$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy','xiaoming@booktrading.com','13800000005','https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming',0,1,4.0,1,'2026-01-09 05:04:29','2026-01-09 07:34:17',0),(9,'纪兆煜','$2a$10$8vodVRxGFeuJIQSwu9402OzdGkH1sXAgjNUk3/FjBH0PiqIrHpHdW','1013003906@qq.com','13789822207',NULL,0,1,0.0,0,'2026-01-09 21:15:03','2026-01-09 21:15:03',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-15 13:59:01
