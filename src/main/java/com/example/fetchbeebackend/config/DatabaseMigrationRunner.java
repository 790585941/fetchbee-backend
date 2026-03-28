package com.example.fetchbeebackend.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Minimal startup migration to keep the schema aligned with the
 * admin-review features introduced on the backend.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationRunner {

    private static final String ORDER_TABLE = "order";

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrate() {
        ensureOrderReviewColumns();
        ensureRightsImageColumnType();
    }

    private void ensureOrderReviewColumns() {
        try {
            boolean reviewStatusAdded = ensureColumnExists(
                    "review_status",
                    "ALTER TABLE `order` ADD COLUMN `review_status` tinyint(0) NOT NULL DEFAULT 0 " +
                            "COMMENT 'Audit status: 0-PENDING_REVIEW, 1-APPROVED, 2-REJECTED' AFTER `cancel_reason`"
            );
            if (reviewStatusAdded) {
                jdbcTemplate.execute("ALTER TABLE `order` ADD INDEX `idx_review_status`(`review_status`) USING BTREE");
                log.info("Added review_status column (and index) for `{}` table", ORDER_TABLE);
            }

            ensureColumnExists(
                    "review_remark",
                    "ALTER TABLE `order` ADD COLUMN `review_remark` varchar(500) NULL DEFAULT NULL " +
                            "COMMENT 'Audit remark (rejection reason, etc.)' AFTER `review_status`"
            );
            ensureColumnExists(
                    "review_time",
                    "ALTER TABLE `order` ADD COLUMN `review_time` datetime(0) NULL DEFAULT NULL " +
                            "COMMENT 'Audit time' AFTER `review_remark`"
            );
            ensureColumnExists(
                    "reviewer_id",
                    "ALTER TABLE `order` ADD COLUMN `reviewer_id` bigint(0) NULL DEFAULT NULL " +
                            "COMMENT 'Auditor user ID' AFTER `review_time`"
            );

            ensureOrderStatusDefault();
            initializeReviewStatusData();
        } catch (Exception ex) {
            log.error("Failed to run automatic database migration for admin review columns", ex);
        }
    }

    private boolean ensureColumnExists(String columnName, String ddlSql) {
        if (columnExists(columnName)) {
            return false;
        }
        jdbcTemplate.execute(ddlSql);
        log.info("Added column `{}` to `{}` table", columnName, ORDER_TABLE);
        return true;
    }

    private boolean columnExists(String columnName) {
        String sql = "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() AND table_name = ? AND LOWER(column_name) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ORDER_TABLE, columnName);
        return count != null && count > 0;
    }

    private void ensureOrderStatusDefault() {
        String sql = "SELECT COLUMN_DEFAULT FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = 'status'";
        try {
            String defaultValue = jdbcTemplate.queryForObject(sql, String.class, ORDER_TABLE);
            if (!"0".equals(defaultValue)) {
                jdbcTemplate.execute(
                        "ALTER TABLE `order` MODIFY COLUMN `status` tinyint(0) NOT NULL DEFAULT 0 " +
                                "COMMENT 'Status: 0-pending review, 1-awaiting accept, 2-accepted, 3-awaiting confirm, 4-completed, 5-cancelled, 6-review rejected'"
                );
                log.info("Updated `status` column default for `{}` table", ORDER_TABLE);
            }
        } catch (Exception ex) {
            log.warn("Unable to verify or update `status` column default", ex);
        }
    }

    private void initializeReviewStatusData() {
        try {
            jdbcTemplate.execute(
                    "UPDATE `order` SET `review_status` = CASE " +
                            "WHEN status = 0 THEN 0 " +
                            "WHEN status = 6 THEN 2 " +
                            "ELSE 1 END " +
                            "WHERE `review_status` IS NULL"
            );
        } catch (Exception ex) {
            log.warn("Failed to back-fill review_status data", ex);
        }
    }

    private void ensureRightsImageColumnType() {
        try {
            if (!columnExists("rights_image")) {
                return;
            }

            String dataTypeSql = "SELECT DATA_TYPE FROM information_schema.columns " +
                    "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = 'rights_image'";
            String dataType = jdbcTemplate.queryForObject(dataTypeSql, String.class, ORDER_TABLE);
            if (dataType != null && !"longtext".equalsIgnoreCase(dataType)) {
                jdbcTemplate.execute(
                        "ALTER TABLE `order` MODIFY COLUMN `rights_image` LONGTEXT " +
                                "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL " +
                                "COMMENT 'Rights proof image (Base64)'"
                );
                log.info("Updated `rights_image` column type to LONGTEXT for `{}` table", ORDER_TABLE);
            }
        } catch (Exception ex) {
            log.warn("Failed to verify or update `rights_image` column type", ex);
        }
    }
}
