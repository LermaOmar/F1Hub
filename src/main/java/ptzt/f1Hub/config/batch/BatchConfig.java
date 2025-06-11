package ptzt.f1Hub.config.batch;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import ptzt.f1Hub.domain.models.copy.Driver;
import ptzt.f1Hub.domain.models.copy.AuctionableEntity;
import ptzt.f1Hub.domain.models.original.Account;
import ptzt.f1Hub.domain.models.original.AppUser;
import ptzt.f1Hub.domain.models.original.Budget;
import ptzt.f1Hub.domain.models.original.League;
import ptzt.f1Hub.domain.models.original.LineUp;
import ptzt.f1Hub.domain.models.original.VerificationToken;
import ptzt.f1Hub.domain.models.original.market.Market;
import ptzt.f1Hub.domain.models.original.market.Offer;
import ptzt.f1Hub.instraestructure.dto.batch.DriverAuctionableJoin;
import ptzt.f1Hub.instraestructure.dto.batch.MarketItemJoin;
import ptzt.f1Hub.instraestructure.dto.batch.TeamAuctionableJoin;


@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    //
    // ─── ACCOUNT ──────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<Account> readerAccount(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<Account> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, password, email, username, active FROM ACCOUNT");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Account.class));
        return reader;
    }

    @Bean
    public ItemProcessor<Account, ptzt.f1Hub.domain.models.copy.Account> processorAccountToAccount() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.Account copy = new ptzt.f1Hub.domain.models.copy.Account();
            copy.setId(original.getId());
            copy.setPassword(original.getPassword());
            copy.setEmail(original.getEmail());
            copy.setUsername(original.getUsername());
            copy.setActive(original.isActive());
            copy.setRoles(new HashSet<>(original.getRoles()));
            if (original.getAppUser() != null) {
                ptzt.f1Hub.domain.models.copy.AppUser au = new ptzt.f1Hub.domain.models.copy.AppUser();
                au.setId(original.getAppUser().getId());
                copy.setAppUser(au);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Account> writerAccount(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Account> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO ACCOUNT (id, password, email, username, active) " +
                        "KEY(id) VALUES (?, ?, ?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getPassword());
            ps.setString(3, item.getEmail());
            ps.setString(4, item.getUsername());
            ps.setBoolean(5, item.isActive());
        });
        return writer;
    }

    @Bean
    public Step stepAccount(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            JdbcCursorItemReader<Account> readerAccount,
                            ItemProcessor<Account, ptzt.f1Hub.domain.models.copy.Account> processorAccountToAccount,
                            JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Account> writerAccount) {
        return new StepBuilder("stepAccount", jobRepository)
                .<Account, ptzt.f1Hub.domain.models.copy.Account>chunk(100, transactionManager)
                .reader(readerAccount)
                .processor(processorAccountToAccount)
                .writer(writerAccount)
                .build();
    }

    //
    // ─── APP_USER ──────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<AppUser> readerAppUser(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<AppUser> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, account_id FROM APP_USER");
        reader.setRowMapper(new BeanPropertyRowMapper<>(AppUser.class));
        return reader;
    }

    @Bean
    public ItemProcessor<AppUser, ptzt.f1Hub.domain.models.copy.AppUser> processorAppUserToAppUser() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.AppUser copy = new ptzt.f1Hub.domain.models.copy.AppUser();
            copy.setId(original.getId());
            if (original.getAccount() != null) {
                ptzt.f1Hub.domain.models.copy.Account a = new ptzt.f1Hub.domain.models.copy.Account();
                a.setId(original.getAccount().getId());
                copy.setAccount(a);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AppUser> writerAppUser(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AppUser> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO APP_USER (id, account_id) " +
                        "KEY(id) VALUES (?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            if (item.getAccount() != null) {
                ps.setLong(2, item.getAccount().getId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
        });
        return writer;
    }

    @Bean
    public Step stepAppUser(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            JdbcCursorItemReader<AppUser> readerAppUser,
                            ItemProcessor<AppUser, ptzt.f1Hub.domain.models.copy.AppUser> processorAppUserToAppUser,
                            JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AppUser> writerAppUser) {
        return new StepBuilder("stepAppUser", jobRepository)
                .<AppUser, ptzt.f1Hub.domain.models.copy.AppUser>chunk(100, transactionManager)
                .reader(readerAppUser)
                .processor(processorAppUserToAppUser)
                .writer(writerAppUser)
                .build();
    }

    //
    // ─── VERIFICATION_TOKEN ─────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<VerificationToken> readerVerificationToken(
            @Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<VerificationToken> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, token, expires_at, confirmed_at, account_id FROM VERIFICATION_TOKEN");
        reader.setRowMapper(new BeanPropertyRowMapper<>(VerificationToken.class));
        return reader;
    }

    @Bean
    public ItemProcessor<VerificationToken, ptzt.f1Hub.domain.models.copy.VerificationToken>
    processorVerificationTokenToVerificationToken() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.VerificationToken copy = new ptzt.f1Hub.domain.models.copy.VerificationToken();
            copy.setId(original.getId());
            copy.setToken(original.getToken());
            copy.setExpiresAt(original.getExpiresAt());
            copy.setConfirmedAt(original.getConfirmedAt());
            if (original.getAccount() != null) {
                ptzt.f1Hub.domain.models.copy.Account a = new ptzt.f1Hub.domain.models.copy.Account();
                a.setId(original.getAccount().getId());
                copy.setAccount(a);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.VerificationToken>
    writerVerificationToken(@Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.VerificationToken> writer =
                new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO VERIFICATION_TOKEN " +
                        "(id, token, expires_at, confirmed_at, account_id) " +
                        "KEY(id) VALUES (?, ?, ?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getToken());
            ps.setTimestamp(3, item.getExpiresAt() != null ? Timestamp.valueOf(item.getExpiresAt()) : null);
            ps.setTimestamp(4, item.getConfirmedAt() != null ? Timestamp.valueOf(item.getConfirmedAt()) : null);
            if (item.getAccount() != null) {
                ps.setLong(5, item.getAccount().getId());
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
        });
        return writer;
    }

    @Bean
    public Step stepVerificationToken(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                      JdbcCursorItemReader<VerificationToken> readerVerificationToken,
                                      ItemProcessor<VerificationToken, ptzt.f1Hub.domain.models.copy.VerificationToken> processorVerificationTokenToVerificationToken,
                                      JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.VerificationToken> writerVerificationToken) {
        return new StepBuilder("stepVerificationToken", jobRepository)
                .<VerificationToken, ptzt.f1Hub.domain.models.copy.VerificationToken>chunk(100, transactionManager)
                .reader(readerVerificationToken)
                .processor(processorVerificationTokenToVerificationToken)
                .writer(writerVerificationToken)
                .build();
    }

    //
    // ─── LEAGUE ──────────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<League> readerLeague(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<League> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, name FROM LEAGUE");
        reader.setRowMapper(new BeanPropertyRowMapper<>(League.class));
        return reader;
    }

    @Bean
    public ItemProcessor<League, ptzt.f1Hub.domain.models.copy.League> processorLeagueToLeague() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.League copy = new ptzt.f1Hub.domain.models.copy.League();
            copy.setId(original.getId());
            copy.setName(original.getName());
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.League> writerLeague(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.League> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO LEAGUE (id, name) " +
                        "KEY(id) VALUES (?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getName());
        });
        return writer;
    }

    @Bean
    public Step stepLeague(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           JdbcCursorItemReader<League> readerLeague,
                           ItemProcessor<League, ptzt.f1Hub.domain.models.copy.League> processorLeagueToLeague,
                           JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.League> writerLeague) {
        return new StepBuilder("stepLeague", jobRepository)
                .<League, ptzt.f1Hub.domain.models.copy.League>chunk(100, transactionManager)
                .reader(readerLeague)
                .processor(processorLeagueToLeague)
                .writer(writerLeague)
                .build();
    }

    //
    // ─── MARKET ──────────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<Market> readerMarket(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<Market> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, league_id FROM MARKET");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Market.class));
        return reader;
    }

    @Bean
    public ItemProcessor<Market, ptzt.f1Hub.domain.models.copy.market.Market> processorMarketToMarket() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.market.Market copy = new ptzt.f1Hub.domain.models.copy.market.Market();
            copy.setId(original.getId());
            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League lc = new ptzt.f1Hub.domain.models.copy.League();
                lc.setId(original.getLeague().getId());
                copy.setLeague(lc);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Market> writerMarket(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Market> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO MARKET (id, league_id) " +
                        "KEY(id) VALUES (?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            if (item.getLeague() != null) {
                ps.setLong(2, item.getLeague().getId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
        });
        return writer;
    }

    @Bean
    public Step stepMarket(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           JdbcCursorItemReader<Market> readerMarket,
                           ItemProcessor<Market, ptzt.f1Hub.domain.models.copy.market.Market> processorMarketToMarket,
                           JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Market> writerMarket) {
        return new StepBuilder("stepMarket", jobRepository)
                .<Market, ptzt.f1Hub.domain.models.copy.market.Market>chunk(100, transactionManager)
                .reader(readerMarket)
                .processor(processorMarketToMarket)
                .writer(writerMarket)
                .build();
    }

    //
    // ─── READ DRIVER JOINED WITH AUCTIONABLE_ENTITY & CREATE copy.Driver ───────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<DriverAuctionableJoin> readerAuctionableEntityFromDriver(
            @Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<DriverAuctionableJoin> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);

        reader.setSql(
                "SELECT " +
                        "  ae.id              AS ae_id, " +
                        "  ae.active          AS ae_active, " +
                        "  ae.image_url       AS ae_image_url, " +
                        "  ae.nationality     AS ae_nationality, " +
                        "  ae.points          AS ae_points, " +
                        "  ae.previous_points AS ae_previous_points, " +
                        "  ae.price           AS ae_price, " +
                        "  d.name             AS driver_name " +
                        "FROM DRIVER d " +
                        "INNER JOIN AUCTIONABLE_ENTITY ae ON d.id = ae.id"
        );

        reader.setRowMapper((rs, rowNum) -> {
            DriverAuctionableJoin temp = new DriverAuctionableJoin();
            temp.setId(rs.getLong("ae_id"));
            temp.setActive(rs.getBoolean("ae_active"));
            temp.setImageUrl(rs.getString("ae_image_url"));
            temp.setNationality(rs.getString("ae_nationality"));
            temp.setPoints(rs.getLong("ae_points"));
            temp.setPreviousPoints(rs.getLong("ae_previous_points"));
            temp.setPrice(rs.getLong("ae_price"));
            temp.setDriverName(rs.getString("driver_name"));
            return temp;
        });

        return reader;
    }

    @Bean
    public ItemProcessor<DriverAuctionableJoin, ptzt.f1Hub.domain.models.copy.Driver> processorAuctionableFromDriver() {
        return originalJoin -> {
            ptzt.f1Hub.domain.models.copy.Driver driverCopy = new ptzt.f1Hub.domain.models.copy.Driver();

            driverCopy.setId(originalJoin.getId());
            // No hay setter para 'type' en AuctionableEntity, así que lo ponemos en el SQL
            driverCopy.setActive(originalJoin.getActive());
            driverCopy.setImageUrl(originalJoin.getImageUrl());
            driverCopy.setNationality(originalJoin.getNationality());
            driverCopy.setPoints(originalJoin.getPoints());
            driverCopy.setPreviousPoints(originalJoin.getPreviousPoints());
            driverCopy.setPrice(originalJoin.getPrice());
            driverCopy.setName(originalJoin.getDriverName());

            return driverCopy;
        };
    }

    //
    // ─── WRITE DRIVER JOINED WITH AUCTIONABLE_ENTITY TO SECONDARY DATABASE ─────────────────────────────────────────────────────────────────────────
    //

    @Bean
    @Qualifier("writerAEFromDriver")
    public JdbcBatchItemWriter<AuctionableEntity> writerAEFromDriver(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<AuctionableEntity> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO AUCTIONABLE_ENTITY " +
                        "(id, type, active, image_url, nationality, points, previous_points, price) " +
                        "KEY(id) VALUES (?, 'DRIVER', ?, ?, ?, ?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setBoolean(2, item.getActive());
            ps.setString(3, item.getImageUrl());
            ps.setString(4, item.getNationality());
            ps.setLong(5, item.getPoints());
            ps.setLong(6, item.getPreviousPoints());
            ps.setLong(7, item.getPrice());
        });
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Driver> writerDriverFromJoin(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Driver> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO DRIVER (id, name) " +
                        "KEY(id) VALUES (?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getName());
        });
        return writer;
    }

    //
    // ─── DRIVER ──────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public CompositeItemWriter<Driver> compositeWriterFromDriver(
            @Qualifier("writerAEFromDriver") JdbcBatchItemWriter<AuctionableEntity> writerAE,
            JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Driver> writerDr) {

        return new CompositeItemWriterBuilder<Driver>()
                .delegates(Arrays.asList(
                        (items) -> {
                            for (ptzt.f1Hub.domain.models.copy.Driver d : items) {
                                writerAE.write(new Chunk<>(Collections.singletonList((AuctionableEntity) d)));
                            }
                        },
                        writerDr
                ))
                .build();
    }

    @Bean
    public Step stepAuctionableFromDriver(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                          JdbcCursorItemReader<DriverAuctionableJoin> readerAuctionableEntityFromDriver,
                                          ItemProcessor<DriverAuctionableJoin, ptzt.f1Hub.domain.models.copy.Driver> processorAuctionableFromDriver,
                                          CompositeItemWriter<ptzt.f1Hub.domain.models.copy.Driver> compositeWriterFromDriver) {

        return new StepBuilder("stepAuctionableFromDriver", jobRepository)
                .<DriverAuctionableJoin, ptzt.f1Hub.domain.models.copy.Driver>chunk(100, transactionManager)
                .reader(readerAuctionableEntityFromDriver)
                .processor(processorAuctionableFromDriver)
                .writer(compositeWriterFromDriver)
                .build();
    }

    //
    // ─── READ TEAM JOINED WITH AUCTIONABLE_ENTITY & CREATE copy.Driver ──────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<TeamAuctionableJoin> readerAuctionableEntityFromTeam(
            @Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<TeamAuctionableJoin> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);

        reader.setSql(
                "SELECT " +
                        "  ae.id              AS ae_id, " +
                        "  ae.active          AS ae_active, " +
                        "  ae.image_url       AS ae_image_url, " +
                        "  ae.nationality     AS ae_nationality, " +
                        "  ae.points          AS ae_points, " +
                        "  ae.previous_points AS ae_previous_points, " +
                        "  ae.price           AS ae_price, " +
                        "  t.name             AS team_name " +
                        "FROM TEAM t " +
                        "INNER JOIN AUCTIONABLE_ENTITY ae ON t.id = ae.id"
        );

        reader.setRowMapper((rs, rowNum) -> {
            TeamAuctionableJoin temp = new TeamAuctionableJoin();
            temp.setId(rs.getLong("ae_id"));
            temp.setActive(rs.getBoolean("ae_active"));
            temp.setImageUrl(rs.getString("ae_image_url"));
            temp.setNationality(rs.getString("ae_nationality"));
            temp.setPoints(rs.getLong("ae_points"));
            temp.setPreviousPoints(rs.getLong("ae_previous_points"));
            temp.setPrice(rs.getLong("ae_price"));
            temp.setTeamName(rs.getString("team_name"));
            return temp;
        });

        return reader;
    }

    @Bean
    public ItemProcessor<TeamAuctionableJoin, ptzt.f1Hub.domain.models.copy.Team> processorAuctionableFromTeam() {
        return originalJoin -> {
            ptzt.f1Hub.domain.models.copy.Team teamCopy = new ptzt.f1Hub.domain.models.copy.Team();

            teamCopy.setId(originalJoin.getId());
            teamCopy.setActive(originalJoin.getActive());
            teamCopy.setImageUrl(originalJoin.getImageUrl());
            teamCopy.setNationality(originalJoin.getNationality());
            teamCopy.setPoints(originalJoin.getPoints());
            teamCopy.setPreviousPoints(originalJoin.getPreviousPoints());
            teamCopy.setPrice(originalJoin.getPrice());
            teamCopy.setName(originalJoin.getTeamName());

            return teamCopy;
        };
    }

    //
    // ─── WRITE DRIVER JOINED WITH AUCTIONABLE_ENTITY TO SECONDARY DATABASE ────────────────────────────────────────────────────────────────────────
    //

    @Bean
    @Qualifier("writerAEFromTeam")
    public JdbcBatchItemWriter<AuctionableEntity> writerAEFromTeam(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<AuctionableEntity> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO AUCTIONABLE_ENTITY " +
                        "(id, type, active, image_url, nationality, points, previous_points, price) " +
                        "KEY(id) VALUES (?, 'TEAM', ?, ?, ?, ?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setBoolean(2, item.getActive());
            ps.setString(3, item.getImageUrl());
            ps.setString(4, item.getNationality());
            ps.setLong(5, item.getPoints());
            ps.setLong(6, item.getPreviousPoints());
            ps.setLong(7, item.getPrice());
        });
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Team> writerTeamFromJoin(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Team> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO TEAM (id, name) " +
                        "KEY(id) VALUES (?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getName());
        });
        return writer;
    }

    //
    // ─── TEAM ──────────────────────────────────────────────────────────────────────────────────────────
    //


    @Bean
    public CompositeItemWriter<ptzt.f1Hub.domain.models.copy.Team> compositeWriterFromTeam(
            @Qualifier("writerAEFromTeam") JdbcBatchItemWriter<AuctionableEntity> writerAE,
            JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Team> writerTm) {

        return new CompositeItemWriterBuilder<ptzt.f1Hub.domain.models.copy.Team>()
                .delegates(Arrays.asList(
                        (items) -> {
                            for (ptzt.f1Hub.domain.models.copy.Team t : items) {
                                writerAE.write(new Chunk<>(Collections.singletonList((AuctionableEntity) t)));
                            }
                        },
                        writerTm
                ))
                .build();
    }


    @Bean
    public Step stepAuctionableFromTeam(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                        JdbcCursorItemReader<TeamAuctionableJoin> readerAuctionableEntityFromTeam,
                                        ItemProcessor<TeamAuctionableJoin, ptzt.f1Hub.domain.models.copy.Team> processorAuctionableFromTeam,
                                        CompositeItemWriter<ptzt.f1Hub.domain.models.copy.Team> compositeWriterFromTeam) {

        return new StepBuilder("stepAuctionableFromTeam", jobRepository)
                .<TeamAuctionableJoin, ptzt.f1Hub.domain.models.copy.Team>chunk(100, transactionManager)
                .reader(readerAuctionableEntityFromTeam)
                .processor(processorAuctionableFromTeam)
                .writer(compositeWriterFromTeam)
                .build();
    }

    //
    // ─── LINEUP ────────────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<LineUp> readerLineUp(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<LineUp> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, total_points, app_user_id, league_id, team_id FROM LINEUP");
        reader.setRowMapper(new BeanPropertyRowMapper<>(LineUp.class));
        return reader;
    }

    @Bean
    public ItemProcessor<LineUp, ptzt.f1Hub.domain.models.copy.LineUp> processorLineUpToLineUp() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.LineUp copy = new ptzt.f1Hub.domain.models.copy.LineUp();
            copy.setId(original.getId());
            copy.setTotalPoints(original.getTotalPoints());
            if (original.getAppUser() != null) {
                ptzt.f1Hub.domain.models.copy.AppUser au = new ptzt.f1Hub.domain.models.copy.AppUser();
                au.setId(original.getAppUser().getId());
                copy.setAppUser(au);
            }
            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League lc = new ptzt.f1Hub.domain.models.copy.League();
                lc.setId(original.getLeague().getId());
                copy.setLeague(lc);
            }
            if (original.getTeam() != null) {
                ptzt.f1Hub.domain.models.copy.Team tc = new ptzt.f1Hub.domain.models.copy.Team();
                tc.setId(original.getTeam().getId());
                copy.setTeam(tc);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.LineUp> writerLineUp(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.LineUp> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO LINEUP (id, total_points, app_user_id, league_id, team_id) " +
                        "KEY(id) VALUES (?, ?, ?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getTotalPoints());
            if (item.getAppUser() != null) {
                ps.setLong(3, item.getAppUser().getId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            if (item.getLeague() != null) {
                ps.setLong(4, item.getLeague().getId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            if (item.getTeam() != null) {
                ps.setLong(5, item.getTeam().getId());
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
        });
        return writer;
    }

    @Bean
    public Step stepLineUp(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           JdbcCursorItemReader<LineUp> readerLineUp,
                           ItemProcessor<LineUp, ptzt.f1Hub.domain.models.copy.LineUp> processorLineUpToLineUp,
                           JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.LineUp> writerLineUp) {
        return new StepBuilder("stepLineUp", jobRepository)
                .<LineUp, ptzt.f1Hub.domain.models.copy.LineUp>chunk(100, transactionManager)
                .reader(readerLineUp)
                .processor(processorLineUpToLineUp)
                .writer(writerLineUp)
                .build();
    }

    //
    // ─── BUDGET ────────────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<Budget> readerBudget(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<Budget> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, budget_value, app_user_id, league_id FROM BUDGET");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Budget.class));
        return reader;
    }

    @Bean
    public ItemProcessor<Budget, ptzt.f1Hub.domain.models.copy.Budget> processorBudgetToBudget() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.Budget copy = new ptzt.f1Hub.domain.models.copy.Budget();
            copy.setId(original.getId());
            copy.setBudgetValue(original.getBudgetValue());
            if (original.getAppUser() != null) {
                ptzt.f1Hub.domain.models.copy.AppUser au = new ptzt.f1Hub.domain.models.copy.AppUser();
                au.setId(original.getAppUser().getId());
                copy.setAppUser(au);
            }
            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League lc = new ptzt.f1Hub.domain.models.copy.League();
                lc.setId(original.getLeague().getId());
                copy.setLeague(lc);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Budget> writerBudget(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Budget> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO BUDGET (id, budget_value, app_user_id, league_id) " +
                        "KEY(id) VALUES (?, ?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getBudgetValue());
            if (item.getAppUser() != null) {
                ps.setLong(3, item.getAppUser().getId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            if (item.getLeague() != null) {
                ps.setLong(4, item.getLeague().getId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
        });
        return writer;
    }

    @Bean
    public Step stepBudget(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           JdbcCursorItemReader<Budget> readerBudget,
                           ItemProcessor<Budget, ptzt.f1Hub.domain.models.copy.Budget> processorBudgetToBudget,
                           JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Budget> writerBudget) {
        return new StepBuilder("stepBudget", jobRepository)
                .<Budget, ptzt.f1Hub.domain.models.copy.Budget>chunk(100, transactionManager)
                .reader(readerBudget)
                .processor(processorBudgetToBudget)
                .writer(writerBudget)
                .build();
    }

    //
    // ─── MARKET_ITEM ───────────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<MarketItemJoin> readerMarketItem(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<MarketItemJoin> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(
                "SELECT " +
                        "  mi.id                    AS mi_id, " +
                        "  mi.available             AS mi_available, " +
                        "  ae.id                    AS ae_id, " +
                        "  ae.type                  AS ae_type " +
                        "FROM MARKET_ITEM mi " +
                        "JOIN AUCTIONABLE_ENTITY ae ON mi.auctionable_entity_id = ae.id"
        );
        reader.setRowMapper((rs, rowNum) -> {
            MarketItemJoin temp = new MarketItemJoin();
            temp.setId(rs.getLong("mi_id"));
            temp.setAvailable(rs.getBoolean("mi_available"));
            temp.setAeId(rs.getLong("ae_id"));
            temp.setAeType(rs.getString("ae_type"));
            return temp;
        });
        return reader;
    }



    @Bean
    public ItemProcessor<MarketItemJoin, ptzt.f1Hub.domain.models.copy.market.MarketItem> processorMarketItemToMarketItem() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.market.MarketItem copy = new ptzt.f1Hub.domain.models.copy.market.MarketItem();
            copy.setId(original.getId());
            copy.setAvailable(original.getAvailable());

            if (original.getAeId() != null) {
                // Creamos la subclase concreta en función del type
                ptzt.f1Hub.domain.models.copy.AuctionableEntity auctRef;
                if ("DRIVER".equalsIgnoreCase(original.getAeType())) {
                    auctRef = new ptzt.f1Hub.domain.models.copy.Driver();
                } else {
                    auctRef = new ptzt.f1Hub.domain.models.copy.Team();
                }
                auctRef.setId(original.getAeId());
                copy.setAuctionableEntity(auctRef);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.MarketItem> writerMarketItem(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.MarketItem> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO MARKET_ITEM (id, available, auctionable_entity_id) " +
                        "KEY(id) VALUES (?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            if (item.getAvailable() != null) {
                ps.setObject(2, item.getAvailable());
            } else {
                ps.setNull(2, java.sql.Types.BIT);
            }
            if (item.getAuctionableEntity() != null) {
                ps.setLong(3, item.getAuctionableEntity().getId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
        });
        return writer;
    }

    @Bean
    public Step stepMarketItem(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               JdbcCursorItemReader<MarketItemJoin> readerMarketItem,
                               ItemProcessor<MarketItemJoin, ptzt.f1Hub.domain.models.copy.market.MarketItem> processorMarketItemToMarketItem,
                               JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.MarketItem> writerMarketItem) {
        return new StepBuilder("stepMarketItem", jobRepository)
                .<MarketItemJoin, ptzt.f1Hub.domain.models.copy.market.MarketItem>chunk(100, transactionManager)
                .reader(readerMarketItem)
                .processor(processorMarketItemToMarketItem)
                .writer(writerMarketItem)
                .build();
    }

    //
    // ─── OFFER ─────────────────────────────────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public JdbcCursorItemReader<Offer> readerOffer(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<Offer> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, amount, createdat, app_user_id, league_id, market_item_id FROM OFFER");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Offer.class));
        return reader;
    }

    @Bean
    public ItemProcessor<Offer, ptzt.f1Hub.domain.models.copy.market.Offer> processorOfferToOffer() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.market.Offer copy = new ptzt.f1Hub.domain.models.copy.market.Offer();
            copy.setId(original.getId());
            copy.setAmount(original.getAmount());
            copy.setCreatedAt(original.getCreatedAt());
            if (original.getAppUser() != null) {
                ptzt.f1Hub.domain.models.copy.AppUser au = new ptzt.f1Hub.domain.models.copy.AppUser();
                au.setId(original.getAppUser().getId());
                copy.setAppUser(au);
            }
            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League lc = new ptzt.f1Hub.domain.models.copy.League();
                lc.setId(original.getLeague().getId());
                copy.setLeague(lc);
            }
            if (original.getMarketItem() != null) {
                ptzt.f1Hub.domain.models.copy.market.MarketItem mic = new ptzt.f1Hub.domain.models.copy.market.MarketItem();
                mic.setId(original.getMarketItem().getId());
                copy.setMarketItem(mic);
            }
            return copy;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Offer> writerOffer(
            @Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Offer> writer =
                new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql(
                "MERGE INTO OFFER (id, amount, createdat, app_user_id, league_id, market_item_id) " +
                        "KEY(id) VALUES (?, ?, ?, ?, ?, ?)"
        );
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getAmount());
            ps.setTimestamp(3, item.getCreatedAt() != null ? Timestamp.valueOf(item.getCreatedAt()) : null);
            if (item.getAppUser() != null) {
                ps.setLong(4, item.getAppUser().getId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            if (item.getLeague() != null) {
                ps.setLong(5, item.getLeague().getId());
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }
            if (item.getMarketItem() != null) {
                ps.setLong(6, item.getMarketItem().getId());
            } else {
                ps.setNull(6, java.sql.Types.BIGINT);
            }
        });
        return writer;
    }

    @Bean
    public Step stepOffer(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                          JdbcCursorItemReader<Offer> readerOffer,
                          ItemProcessor<Offer, ptzt.f1Hub.domain.models.copy.market.Offer> processorOfferToOffer,
                          JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Offer> writerOffer) {
        return new StepBuilder("stepOffer", jobRepository)
                .<Offer, ptzt.f1Hub.domain.models.copy.market.Offer>chunk(100, transactionManager)
                .reader(readerOffer)
                .processor(processorOfferToOffer)
                .writer(writerOffer)
                .build();
    }

    //
    // ─── FINAL JOB ─────────────────────────────────────────────────────────────────────────────────
    //

    @Bean
    public Job copyEntitiesJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               Step stepAccount,
                               Step stepAppUser,
                               Step stepVerificationToken,
                               Step stepLeague,
                               Step stepMarket,
                               Step stepAuctionableFromDriver,
                               Step stepAuctionableFromTeam,
                               Step stepLineUp,
                               Step stepBudget,
                               Step stepMarketItem,
                               Step stepOffer) {
        return new JobBuilder("copyEntitiesJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepAccount)
                .next(stepAppUser)
                .next(stepVerificationToken)
                .next(stepLeague)
                .next(stepMarket)
                .next(stepAuctionableFromDriver)  // Inserta en AUCTIONABLE_ENTITY con literal 'DRIVER'
                .next(stepAuctionableFromTeam)    // Inserta en AUCTIONABLE_ENTITY con literal 'TEAM'
                .next(stepLineUp)
                .next(stepBudget)
                .next(stepMarketItem)
                .next(stepOffer)
                .build();
    }

}
