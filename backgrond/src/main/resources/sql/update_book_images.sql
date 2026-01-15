-- =============================================
-- 更新书籍封面图片 SQL 脚本
-- 使用 dummyimage.com 占位图服务（更稳定）
-- 执行方式：在 MySQL 中运行此脚本
-- =============================================

-- 文学类书籍 (category_id=1)
UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/4caf50/ffffff&text=活着"]' 
WHERE `title` = '活着' AND `author` = '余华';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/ff9800/ffffff&text=百年孤独"]' 
WHERE `title` = '百年孤独';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/e91e63/ffffff&text=红楼梦"]' 
WHERE `title` = '红楼梦';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/2196f3/ffffff&text=围城"]' 
WHERE `title` = '围城';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/9c27b0/ffffff&text=平凡的世界"]' 
WHERE `title` = '平凡的世界';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/263238/4fc3f7&text=三体"]' 
WHERE `title` = '三体';

-- 教材类书籍 (category_id=2)
UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/3f51b5/ffffff&text=高等数学"]' 
WHERE `title` = '高等数学（第七版）上册';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/00bcd4/ffffff&text=线性代数"]' 
WHERE `title` = '线性代数' AND `author` = '同济大学数学系';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/ff5722/ffffff&text=数据结构"]' 
WHERE `title` = '数据结构（C语言版）';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/03a9f4/ffffff&text=计算机网络"]' 
WHERE `title` = '计算机网络（第7版）';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/607d8b/ffffff&text=操作系统"]' 
WHERE `title` = '操作系统概念';

-- 科技类书籍 (category_id=3)
UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/37474f/ffffff&text=CSAPP"]' 
WHERE `title` = '深入理解计算机系统';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/1a237e/ffffff&text=算法导论"]' 
WHERE `title` = '算法导论';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/ffc107/212121&text=JavaScript"]' 
WHERE `title` = 'JavaScript高级程序设计';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/2196f3/ffffff&text=Python"]' 
WHERE `title` = 'Python编程：从入门到实践';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/4caf50/ffffff&text=Clean+Code"]' 
WHERE `title` = '代码整洁之道';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/ff7043/ffffff&text=人类简史"]' 
WHERE `title` = '人类简史';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/212121/ffffff&text=时间简史"]' 
WHERE `title` = '时间简史';

-- 艺术类书籍 (category_id=4)
UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/bf360c/ffffff&text=艺术的故事"]' 
WHERE `title` = '艺术的故事';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/009688/ffffff&text=设计书"]' 
WHERE `title` = '写给大家看的设计书';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/e91e63/ffffff&text=配色设计"]' 
WHERE `title` = '配色设计原理';

-- 生活类书籍 (category_id=5)
UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/8bc34a/ffffff&text=断舍离"]' 
WHERE `title` = '断舍离';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/ff9800/ffffff&text=小家越住越大"]' 
WHERE `title` = '小家越住越大';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/ffc107/212121&text=好好吃饭"]' 
WHERE `title` = '好好吃饭';

-- Java相关书籍
UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/b71c1c/ffffff&text=Java核心技术"]' 
WHERE `title` = 'Java核心技术 卷I';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/4caf50/ffffff&text=Spring实战"]' 
WHERE `title` = 'Spring实战（第5版）';

UPDATE `book` SET `images` = '["https://dummyimage.com/300x400/ff5722/ffffff&text=Effective+Java"]' 
WHERE `title` = 'Effective Java（第3版）';

-- =============================================
-- 验证更新结果
-- =============================================
SELECT id, title, author, category_id, images FROM `book` ORDER BY category_id, id;
