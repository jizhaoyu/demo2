package com.example.booktrading;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用启动测试
 */
@SpringBootTest(classes = BookTradingApplication.class)
@ActiveProfiles("test")
class BookTradingApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }

}
