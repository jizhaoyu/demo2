-- =============================================
-- 二手书交易管理系统 - 初始化数据脚本
-- =============================================

    USE `book_trading`;

-- =============================================
-- 1. 初始化分类数据
-- =============================================
INSERT INTO `category` (`name`, `sort`) VALUES
('文学', 1),
('教材', 2),
('科技', 3),
('艺术', 4),
('生活', 5),
('其他', 6);

-- =============================================
-- 2. 初始化管理员账户
-- 密码: admin123 (BCrypt加密后)
-- =============================================
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `avatar`, `role`, `status`, `avg_rating`, `rating_count`) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'admin@booktrading.com', '13800000000', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 1, 1, 5.0, 0);

-- =============================================
-- 3. 测试用户数据
-- 密码: 123456 (BCrypt加密后)
-- =============================================
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `avatar`, `role`, `status`, `avg_rating`, `rating_count`) VALUES
('张三', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'zhangsan@booktrading.com', '13800000001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan', 0, 1, 4.8, 5),
('李四', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'lisi@booktrading.com', '13800000002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=lisi', 0, 1, 4.5, 3),
('王五', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'wangwu@booktrading.com', '13800000003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wangwu', 0, 1, 4.2, 2),
('赵六', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'zhaoliu@booktrading.com', '13800000004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhaoliu', 0, 1, 4.9, 8),
('小明', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'xiaoming@booktrading.com', '13800000005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming', 0, 1, 4.0, 1);


UPDATE `user` SET `password` = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW' where id>0;
UPDATE `user` SET `password` = '$2a$10$lv8je8wI2Ouy39OruPXkj.gYobnGfPmuRpMNrihOEuEpfhc2BG2Yy' WHERE id > 0;




-- =============================================
-- 4. 书籍数据
-- =============================================
INSERT INTO `book` (`title`, `author`, `price`, `original_price`, `condition_level`, `description`, `images`, `status`, `user_id`, `category_id`, `view_count`) VALUES
-- 文学类书籍 (category_id=1)
('活着', '余华', 18.00, 45.00, 2, '余华经典作品，九成新，无笔记无划痕，值得收藏。', '["https://img3.doubanio.com/view/subject/l/public/s29053580.jpg"]', 1, 2, 1, 156),
('百年孤独', '加西亚·马尔克斯', 35.00, 68.00, 2, '诺贝尔文学奖作品，精装版，品相很好。', '["https://img2.doubanio.com/view/subject/l/public/s6384944.jpg"]', 1, 2, 1, 89),
('红楼梦', '曹雪芹', 25.00, 59.00, 3, '人民文学出版社版本，八成新，有少量笔记。', '["https://img1.doubanio.com/view/subject/l/public/s1070959.jpg"]', 1, 3, 1, 234),
('围城', '钱钟书', 15.00, 39.00, 2, '经典讽刺小说，九成新，推荐阅读。', '["https://img3.doubanio.com/view/subject/l/public/s1070222.jpg"]', 1, 4, 1, 67),
('平凡的世界', '路遥', 45.00, 108.00, 1, '全新未拆封，三册全套，茅盾文学奖作品。', '["https://img1.doubanio.com/view/subject/l/public/s27913870.jpg"]', 1, 5, 1, 312),

-- 教材类书籍 (category_id=2)
('高等数学（第七版）上册', '同济大学数学系', 20.00, 48.00, 3, '考研必备教材，有部分笔记和习题答案。', '["https://img9.doubanio.com/view/subject/l/public/s28012461.jpg"]', 1, 2, 2, 567),
('线性代数', '同济大学数学系', 12.00, 28.00, 3, '八成新，有课堂笔记，适合考研复习。', '["https://img2.doubanio.com/view/subject/l/public/s28012463.jpg"]', 1, 3, 2, 423),
('数据结构（C语言版）', '严蔚敏', 22.00, 49.00, 2, '计算机专业必修教材，九成新，无笔记。', '["https://img3.doubanio.com/view/subject/l/public/s28012465.jpg"]', 1, 4, 2, 345),
('计算机网络（第7版）', '谢希仁', 28.00, 59.00, 2, '经典教材，九成新，附带课后习题答案。', '["https://img1.doubanio.com/view/subject/l/public/s29634659.jpg"]', 1, 5, 2, 289),
('操作系统概念', 'Abraham Silberschatz', 55.00, 129.00, 1, '全新，英文原版，适合深入学习操作系统。', '["https://img2.doubanio.com/view/subject/l/public/s29634661.jpg"]', 1, 2, 2, 178),

-- 科技类书籍 (category_id=3)
('深入理解计算机系统', 'Randal E.Bryant', 68.00, 139.00, 2, 'CSAPP经典，程序员必读，九成新。', '["https://img9.doubanio.com/view/subject/l/public/s29195878.jpg"]', 1, 3, 3, 456),
('算法导论', 'Thomas H.Cormen', 75.00, 168.00, 2, '算法圣经，第三版，品相良好。', '["https://img3.doubanio.com/view/subject/l/public/s25648004.jpg"]', 1, 4, 3, 389),
('JavaScript高级程序设计', 'Nicholas C.Zakas', 45.00, 129.00, 2, '红宝书第四版，前端必备，九成新。', '["https://img2.doubanio.com/view/subject/l/public/s33703494.jpg"]', 1, 5, 3, 267),
('Python编程：从入门到实践', 'Eric Matthes', 35.00, 89.00, 1, '全新，适合Python初学者。', '["https://img1.doubanio.com/view/subject/l/public/s29643040.jpg"]', 1, 2, 3, 534),
('代码整洁之道', 'Robert C.Martin', 38.00, 79.00, 2, '程序员必读，教你写出优雅代码。', '["https://img3.doubanio.com/view/subject/l/public/s4103991.jpg"]', 1, 3, 3, 198),

-- 艺术类书籍 (category_id=4)
('艺术的故事', '贡布里希', 85.00, 188.00, 2, '艺术史入门经典，精装大开本，九成新。', '["https://img1.doubanio.com/view/subject/l/public/s3219163.jpg"]', 1, 4, 4, 145),
('写给大家看的设计书', 'Robin Williams', 28.00, 59.00, 2, '设计入门必读，通俗易懂。', '["https://img2.doubanio.com/view/subject/l/public/s33463248.jpg"]', 1, 5, 4, 234),
('配色设计原理', '伊达千代', 32.00, 68.00, 1, '全新，日本设计师经典配色指南。', '["https://img3.doubanio.com/view/subject/l/public/s4523623.jpg"]', 1, 2, 4, 167),

-- 生活类书籍 (category_id=5)
('断舍离', '山下英子', 18.00, 45.00, 2, '整理收纳经典，九成新，改变生活方式。', '["https://img1.doubanio.com/view/subject/l/public/s27279654.jpg"]', 1, 3, 5, 289),
('小家越住越大', '逯薇', 25.00, 59.00, 2, '家居收纳指南，实用性强。', '["https://img2.doubanio.com/view/subject/l/public/s29457963.jpg"]', 1, 4, 5, 178),
('好好吃饭', '蔡澜', 22.00, 48.00, 1, '全新，美食家蔡澜的饮食哲学。', '["https://img3.doubanio.com/view/subject/l/public/s29634663.jpg"]', 1, 5, 5, 123),

-- admin用户的书籍
('Java核心技术 卷I', 'Cay S.Horstmann', 58.00, 149.00, 2, '管理员发布的测试书籍，Java经典教材，九成新。', '["https://img3.doubanio.com/view/subject/l/public/s28297945.jpg"]', 1, 1, 2, 123),
('Spring实战（第5版）', 'Craig Walls', 48.00, 109.00, 2, 'Spring框架权威指南，九成新，适合Java开发者。', '["https://img1.doubanio.com/view/subject/l/public/s29957765.jpg"]', 1, 1, 3, 89),
('Effective Java（第3版）', 'Joshua Bloch', 65.00, 129.00, 1, '全新，Java编程最佳实践，程序员必读。', '["https://img2.doubanio.com/view/subject/l/public/s29633315.jpg"]', 1, 1, 3, 156),

-- 已售和下架的书籍
('三体', '刘慈欣', 55.00, 128.00, 1, '全新三册套装，科幻巨作。', '["https://img9.doubanio.com/view/subject/l/public/s28357056.jpg"]', 3, 2, 1, 678),
('人类简史', '尤瓦尔·赫拉利', 42.00, 98.00, 2, '九成新，畅销历史读物。', '["https://img3.doubanio.com/view/subject/l/public/s27814883.jpg"]', 3, 3, 3, 456),
('时间简史', '史蒂芬·霍金', 28.00, 58.00, 3, '八成新，科普经典。', '["https://img1.doubanio.com/view/subject/l/public/s29634665.jpg"]', 4, 4, 3, 234);

-- =============================================
-- 5. 收藏数据
-- =============================================
INSERT INTO `favorite` (`user_id`, `book_id`) VALUES
(2, 3), (2, 4), (2, 11), (2, 12),
(3, 1), (3, 2), (3, 5), (3, 13),
(4, 6), (4, 7), (4, 8), (4, 14),
(5, 9), (5, 10), (5, 15), (5, 16),
(6, 1), (6, 11), (6, 17), (6, 18);

-- =============================================
-- 6. 订单数据
-- =============================================
INSERT INTO `orders` (`order_no`, `book_id`, `buyer_id`, `seller_id`, `price`, `status`, `shipping_address`, `shipping_info`) VALUES
-- 已完成订单
('ORD20250101000001', 22, 3, 2, 55.00, 5, '北京市海淀区中关村大街1号 张三 13800000001', '顺丰快递 SF1234567890'),
('ORD20250101000002', 23, 4, 3, 42.00, 5, '上海市浦东新区陆家嘴环路1000号 李四 13800000002', '中通快递 ZT9876543210'),
-- 已发货订单
('ORD20250105000001', 1, 5, 2, 18.00, 4, '广州市天河区天河路385号 王五 13800000003', '韵达快递 YD1122334455'),
-- 已支付订单
('ORD20250106000001', 6, 6, 2, 20.00, 3, '深圳市南山区科技园南区 赵六 13800000004', NULL),
-- 已确认订单
('ORD20250107000001', 11, 2, 3, 68.00, 2, '杭州市西湖区文三路269号 张三 13800000001', NULL),
-- 待确认订单
('ORD20250108000001', 16, 3, 4, 85.00, 1, '成都市武侯区天府大道北段1700号 李四 13800000002', NULL),
('ORD20250108000002', 12, 5, 4, 75.00, 1, '武汉市洪山区珞喻路1037号 王五 13800000003', NULL),
-- 已取消订单
('ORD20250102000001', 24, 6, 4, 28.00, 6, '南京市鼓楼区汉口路22号 赵六 13800000004', NULL);

-- =============================================
-- 7. 评价数据
-- =============================================
INSERT INTO `review` (`order_id`, `reviewer_id`, `reviewee_id`, `rating`, `content`) VALUES
-- 订单1的评价（买家评价卖家）
(1, 3, 2, 5, '书籍品相很好，发货速度快，卖家服务态度很好，推荐！'),
-- 订单1的评价（卖家评价买家）
(1, 2, 3, 5, '买家很爽快，沟通顺畅，好评！'),
-- 订单2的评价
(2, 4, 3, 4, '书籍质量不错，物流稍慢，整体满意。'),
(2, 3, 4, 5, '买家很有礼貌，交易愉快。');

-- =============================================
-- 8. 消息数据
-- =============================================
INSERT INTO `message` (`sender_id`, `receiver_id`, `content`, `is_read`) VALUES
-- 张三和李四的对话
(2, 3, '你好，请问《活着》这本书还在吗？', 1),
(3, 2, '在的，品相很好，有兴趣可以下单。', 1),
(2, 3, '好的，我看看，价格能优惠点吗？', 1),
(3, 2, '已经是最低价了，包邮哦。', 1),
(2, 3, '行，那我下单了。', 1),

-- 王五和赵六的对话
(4, 5, '请问《算法导论》有课后习题答案吗？', 1),
(5, 4, '有的，书里夹着一份打印的答案。', 1),
(4, 5, '太好了，我想买这本书。', 0),

-- 小明和张三的对话
(6, 2, '《高等数学》笔记多吗？会影响阅读吗？', 1),
(2, 6, '笔记主要在重点章节，不影响阅读，反而有助于理解。', 1),
(6, 2, '好的，谢谢，我考虑一下。', 0),

-- 李四和王五的对话
(3, 4, '你好，《数据结构》这本书能便宜点吗？', 1),
(4, 3, '可以的，20块包邮怎么样？', 1),
(3, 4, '成交！我这就下单。', 0),

-- 系统消息模拟
(1, 2, '欢迎加入二手书交易平台，祝您交易愉快！', 1),
(1, 3, '欢迎加入二手书交易平台，祝您交易愉快！', 1),
(1, 4, '欢迎加入二手书交易平台，祝您交易愉快！', 1),
(1, 5, '欢迎加入二手书交易平台，祝您交易愉快！', 1),
(1, 6, '欢迎加入二手书交易平台，祝您交易愉快！', 1);





INSERT INTO `book` (`title`, `author`, `price`, `original_price`, `condition_level`, `description`, `images`, `status`, `user_id`, `category_id`, `view_count`) VALUES
                                                                                                                                                                    ('Java核心技术 卷I', 'Cay S.Horstmann', 58.00, 149.00, 2, '管理员发布的测试书籍，Java经典教材，九成新。', '["https://img3.doubanio.com/view/subject/l/public/s28297945.jpg"]', 1, 1, 2, 123),
                                                                                                                                                                    ('Spring实战（第5版）', 'Craig Walls', 48.00, 109.00, 2, 'Spring框架权威指南，九成新，适合Java开发者。', '["https://img1.doubanio.com/view/subject/l/public/s29957765.jpg"]', 1, 1, 3, 89),
                                                                                                                                                                    ('Effective Java（第3版）', 'Joshua Bloch', 65.00, 129.00, 1, '全新，Java编程最佳实践，程序员必读。', '["https://img2.doubanio.com/view/subject/l/public/s29633315.jpg"]', 1, 1, 3, 156);
INSERT INTO `book` (`title`, `author`, `price`, `original_price`, `condition_level`, `description`, `images`, `status`, `user_id`, `category_id`, `view_count`) VALUES
                                                                                                                                                                    ('Java核心技术 卷I', 'Cay S.Horstmann', 58.00, 149.00, 2, '管理员发布的测试书籍，Java经典教材，九成新。', '["https://img3.doubanio.com/view/subject/l/public/s28297945.jpg"]', 1, 1, 2, 123),
                                                                                                                                                                    ('Spring实战（第5版）', 'Craig Walls', 48.00, 109.00, 2, 'Spring框架权威指南，九成新，适合Java开发者。', '["https://img1.doubanio.com/view/subject/l/public/s29957765.jpg"]', 1, 1, 3, 89),
                                                                                                                                                                    ('Effective Java（第3版）', 'Joshua Bloch', 65.00, 129.00, 1, '全新，Java编程最佳实践，程序员必读。', '["https://img2.doubanio.com/view/subject/l/public/s29633315.jpg"]', 1, 1, 3, 156);

