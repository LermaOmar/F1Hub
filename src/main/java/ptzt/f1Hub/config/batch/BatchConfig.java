package ptzt.f1Hub.config.batch;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import ptzt.f1Hub.domain.models.original.*;
import ptzt.f1Hub.domain.models.original.market.*;
import ptzt.f1Hub.domain.models.original.Driver;

@Configuration
@Slf4j
public class BatchConfig {


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
                ptzt.f1Hub.domain.models.copy.AppUser appUserCopy = new ptzt.f1Hub.domain.models.copy.AppUser();
                appUserCopy.setId(original.getAppUser().getId());
                copy.setAppUser(appUserCopy);
            }

            Set<ptzt.f1Hub.domain.models.copy.VerificationToken> tokenCopies = new HashSet<>();
            for (VerificationToken token : original.getVerificationTokens()) {
                ptzt.f1Hub.domain.models.copy.VerificationToken tokenCopy = new ptzt.f1Hub.domain.models.copy.VerificationToken();
                tokenCopy.setId(token.getId());
                tokenCopy.setToken(token.getToken());
                tokenCopy.setExpiresAt(token.getExpiresAt());
                tokenCopy.setConfirmedAt(token.getConfirmedAt());
                tokenCopy.setAccount(copy); // set back-reference
                tokenCopies.add(tokenCopy);
            }
            copy.setVerificationTokens(tokenCopies);
            return copy;
        };
    }

    @Bean
    public ItemProcessor<AppUser, ptzt.f1Hub.domain.models.copy.AppUser> processorAppUserToAppUser() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.AppUser copy = new ptzt.f1Hub.domain.models.copy.AppUser();
            copy.setId(original.getId());

            if (original.getAccount() != null) {
                ptzt.f1Hub.domain.models.copy.Account copyAccount = new ptzt.f1Hub.domain.models.copy.Account();
                copyAccount.setId(original.getAccount().getId());
                copy.setAccount(copyAccount);
            }

            if (original.getLineUps() != null) {
                Set<ptzt.f1Hub.domain.models.copy.LineUp> lineUpCopies = original.getLineUps().stream().map(lineUp -> {
                    ptzt.f1Hub.domain.models.copy.LineUp copyLineUp = new ptzt.f1Hub.domain.models.copy.LineUp();
                    copyLineUp.setId(lineUp.getId());
                    return copyLineUp;
                }).collect(Collectors.toSet());
                copy.setLineUps(lineUpCopies);
            }

            if (original.getOffers() != null) {
                Set<ptzt.f1Hub.domain.models.copy.market.Offer> offerCopies = original.getOffers().stream().map(offer -> {
                    ptzt.f1Hub.domain.models.copy.market.Offer copyOffer = new ptzt.f1Hub.domain.models.copy.market.Offer();
                    copyOffer.setId(offer.getId());
                    return copyOffer;
                }).collect(Collectors.toSet());
                copy.setOffers(offerCopies);
            }

            if (original.getBudgets() != null) {
                Set<ptzt.f1Hub.domain.models.copy.Budget> budgetCopies = original.getBudgets().stream().map(budget -> {
                    ptzt.f1Hub.domain.models.copy.Budget copyBudget = new ptzt.f1Hub.domain.models.copy.Budget();
                    copyBudget.setId(budget.getId());
                    return copyBudget;
                }).collect(Collectors.toSet());
                copy.setBudgets(budgetCopies);
            }

            return copy;
        };
    }


    @Bean
    public ItemProcessor<AuctionableEntity, ptzt.f1Hub.domain.models.copy.AuctionableEntity> processorAuctionableEntity() {
        return item -> {
            ptzt.f1Hub.domain.models.copy.AuctionableEntity copy;

            if (item instanceof Driver originalDriver) {
                ptzt.f1Hub.domain.models.copy.Driver copyDriver = new ptzt.f1Hub.domain.models.copy.Driver();
                copyDriver.setName(originalDriver.getName());

                if (originalDriver.getLineUps() != null) {
                    Set<ptzt.f1Hub.domain.models.copy.LineUp> copyLineUps = originalDriver.getLineUps().stream().map(lineUp -> {
                        ptzt.f1Hub.domain.models.copy.LineUp copyLineUp = new ptzt.f1Hub.domain.models.copy.LineUp();
                        copyLineUp.setId(lineUp.getId());
                        return copyLineUp;
                    }).collect(Collectors.toSet());
                    copyDriver.setLineUps(copyLineUps);
                }

                copy = copyDriver;

            } else if (item instanceof Team originalTeam) {
                ptzt.f1Hub.domain.models.copy.Team copyTeam = new ptzt.f1Hub.domain.models.copy.Team();
                copyTeam.setName(originalTeam.getName());

                if (originalTeam.getLineUp() != null) {
                    ptzt.f1Hub.domain.models.copy.LineUp copyLineUp = new ptzt.f1Hub.domain.models.copy.LineUp();
                    copyLineUp.setId(originalTeam.getLineUp().getId());
                    copyTeam.setLineUp(copyLineUp);
                }

                copy = copyTeam;

            } else {
                log.error("AuctionableEntity type not registered");
                throw new RuntimeException("AuctionableEntity type not registered");
            }

            copy.setId(item.getId());
            copy.setNationality(item.getNationality());
            copy.setPrice(item.getPrice());
            copy.setActive(item.getActive());
            copy.setPoints(item.getPoints());
            copy.setPreviousPoints(item.getPreviousPoints());
            copy.setImageUrl(item.getImageUrl());

            if (item.getMarketItems() != null) {
                Set<ptzt.f1Hub.domain.models.copy.market.MarketItem> copyMarketItems = item.getMarketItems().stream().map(marketItem -> {
                    ptzt.f1Hub.domain.models.copy.market.MarketItem copyItem = new ptzt.f1Hub.domain.models.copy.market.MarketItem();
                    copyItem.setId(marketItem.getId());
                    return copyItem;
                }).collect(Collectors.toSet());
                copy.setMarketItems(copyMarketItems);
            }

            return copy;
        };
    }



    @Bean
    public ItemProcessor<Driver, ptzt.f1Hub.domain.models.copy.Driver> processorDriverToDriver() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.Driver copy = new ptzt.f1Hub.domain.models.copy.Driver();
            copy.setId(original.getId());
            copy.setName(original.getName());
            copy.setNationality(original.getNationality());
            copy.setPrice(original.getPrice());
            copy.setPoints(original.getPoints());
            copy.setPreviousPoints(original.getPreviousPoints());
            copy.setActive(original.getActive());
            copy.setImageUrl(original.getImageUrl());

            // Clonar lineUps solo por ID para evitar problemas de persistencia
            if (original.getLineUps() != null) {
                Set<ptzt.f1Hub.domain.models.copy.LineUp> copyLineUps = original.getLineUps().stream().map(lineUp -> {
                    ptzt.f1Hub.domain.models.copy.LineUp copyLineUp = new ptzt.f1Hub.domain.models.copy.LineUp();
                    copyLineUp.setId(lineUp.getId());
                    return copyLineUp;
                }).collect(Collectors.toSet());
                copy.setLineUps(copyLineUps);
            }

            return copy;
        };
    }


    @Bean
    public ItemProcessor<League, ptzt.f1Hub.domain.models.copy.League> processorLeagueToLeague() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.League copy = new ptzt.f1Hub.domain.models.copy.League();
            copy.setId(original.getId());
            copy.setName(original.getName());

            if (original.getMarket() != null) {
                ptzt.f1Hub.domain.models.copy.market.Market copyMarket = new ptzt.f1Hub.domain.models.copy.market.Market();
                copyMarket.setId(original.getMarket().getId());
                copy.setMarket(copyMarket);
            }

            return copy;
        };
    }


    @Bean
    public ItemProcessor<Team, ptzt.f1Hub.domain.models.copy.Team> processorTeamToTeam() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.Team copy = new ptzt.f1Hub.domain.models.copy.Team();
            copy.setId(original.getId());
            copy.setName(original.getName());
            copy.setNationality(original.getNationality());
            copy.setPrice(original.getPrice());
            copy.setPoints(original.getPoints());
            copy.setPreviousPoints(original.getPreviousPoints());
            copy.setActive(original.getActive());
            copy.setImageUrl(original.getImageUrl());

            if (original.getLineUp() != null) {
                ptzt.f1Hub.domain.models.copy.LineUp copyLineUp = new ptzt.f1Hub.domain.models.copy.LineUp();
                copyLineUp.setId(original.getLineUp().getId());
                copy.setLineUp(copyLineUp);
            }

            return copy;
        };
    }


    @Bean
    public ItemProcessor<Market, ptzt.f1Hub.domain.models.copy.market.Market> processorMarketToMarket() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.market.Market copy = new ptzt.f1Hub.domain.models.copy.market.Market();
            copy.setId(original.getId());

            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League copyLeague = new ptzt.f1Hub.domain.models.copy.League();
                copyLeague.setId(original.getLeague().getId());
                copy.setLeague(copyLeague);
            }

            return copy;
        };
    }


    @Bean
    public ItemProcessor<Budget, ptzt.f1Hub.domain.models.copy.Budget> processorBudgetToBudget() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.Budget copy = new ptzt.f1Hub.domain.models.copy.Budget();
            copy.setId(original.getId());
            copy.setBudgetValue(original.getBudgetValue());

            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League league = new ptzt.f1Hub.domain.models.copy.League();
                league.setId(original.getLeague().getId());
                copy.setLeague(league);
            }

            if (original.getAppUser() != null) {
                ptzt.f1Hub.domain.models.copy.AppUser appUser = new ptzt.f1Hub.domain.models.copy.AppUser();
                appUser.setId(original.getAppUser().getId());
                copy.setAppUser(appUser);
            }

            return copy;
        };
    }


    @Bean
    public ItemProcessor<LineUp, ptzt.f1Hub.domain.models.copy.LineUp> processorLineUpToLineUp() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.LineUp copy = new ptzt.f1Hub.domain.models.copy.LineUp();
            copy.setId(original.getId());
            copy.setTotalPoints(original.getTotalPoints());

            // AppUser
            if (original.getAppUser() != null) {
                ptzt.f1Hub.domain.models.copy.AppUser appUser = new ptzt.f1Hub.domain.models.copy.AppUser();
                appUser.setId(original.getAppUser().getId());
                copy.setAppUser(appUser);
            }

            // League
            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League league = new ptzt.f1Hub.domain.models.copy.League();
                league.setId(original.getLeague().getId());
                copy.setLeague(league);
            }

            // Team
            if (original.getTeam() != null) {
                ptzt.f1Hub.domain.models.copy.Team team = new ptzt.f1Hub.domain.models.copy.Team();
                team.setId(original.getTeam().getId());
                copy.setTeam(team);
            }

            // Drivers
            Set<ptzt.f1Hub.domain.models.copy.Driver> copiedDrivers = new HashSet<>();
            for (ptzt.f1Hub.domain.models.original.Driver d : original.getDrivers()) {
                ptzt.f1Hub.domain.models.copy.Driver driver = new ptzt.f1Hub.domain.models.copy.Driver();
                driver.setId(d.getId());
                copiedDrivers.add(driver);
            }
            copy.setDrivers(copiedDrivers);

            return copy;
        };
    }


    @Bean
    public ItemProcessor<VerificationToken, ptzt.f1Hub.domain.models.copy.VerificationToken> processorVerificationTokenToVerificationToken() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.VerificationToken copy = new ptzt.f1Hub.domain.models.copy.VerificationToken();
            copy.setId(original.getId());
            copy.setToken(original.getToken());
            copy.setExpiresAt(original.getExpiresAt());
            copy.setConfirmedAt(original.getConfirmedAt());

            if (original.getAccount() != null) {
                ptzt.f1Hub.domain.models.copy.Account copyAccount = new ptzt.f1Hub.domain.models.copy.Account();
                copyAccount.setId(original.getAccount().getId());
                copy.setAccount(copyAccount);
            }

            return copy;
        };
    }


    @Bean
    public ItemProcessor<MarketItem, ptzt.f1Hub.domain.models.copy.market.MarketItem> processorMarketItemToMarketItem() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.market.MarketItem copy = new ptzt.f1Hub.domain.models.copy.market.MarketItem();
            copy.setId(original.getId());
            copy.setAvailable(original.getAvailable());

            // AuctionableEntity
            if (original.getAuctionableEntity() != null) {
                ptzt.f1Hub.domain.models.copy.AuctionableEntity entity = null;
                if (original.getAuctionableEntity() instanceof ptzt.f1Hub.domain.models.original.Driver driver) {
                    ptzt.f1Hub.domain.models.copy.Driver copyDriver = new ptzt.f1Hub.domain.models.copy.Driver();
                    copyDriver.setId(driver.getId());
                    entity = copyDriver;
                } else if (original.getAuctionableEntity() instanceof ptzt.f1Hub.domain.models.original.Team team) {
                    ptzt.f1Hub.domain.models.copy.Team copyTeam = new ptzt.f1Hub.domain.models.copy.Team();
                    copyTeam.setId(team.getId());
                    entity = copyTeam;
                }
                copy.setAuctionableEntity(entity);
            }

            // Markets
            Set<ptzt.f1Hub.domain.models.copy.market.Market> copiedMarkets = new HashSet<>();
            for (ptzt.f1Hub.domain.models.original.market.Market m : original.getMarkets()) {
                ptzt.f1Hub.domain.models.copy.market.Market copyMarket = new ptzt.f1Hub.domain.models.copy.market.Market();
                copyMarket.setId(m.getId());
                copiedMarkets.add(copyMarket);
            }
            copy.setMarkets(copiedMarkets);

            return copy;
        };
    }


    @Bean
    public ItemProcessor<Offer, ptzt.f1Hub.domain.models.copy.market.Offer> processorOfferToOffer() {
        return original -> {
            ptzt.f1Hub.domain.models.copy.market.Offer copy = new ptzt.f1Hub.domain.models.copy.market.Offer();
            copy.setId(original.getId());
            copy.setAmount(original.getAmount());
            copy.setCreatedAt(original.getCreatedAt());

            if (original.getAppUser() != null) {
                ptzt.f1Hub.domain.models.copy.AppUser copyUser = new ptzt.f1Hub.domain.models.copy.AppUser();
                copyUser.setId(original.getAppUser().getId());
                copy.setAppUser(copyUser);
            }

            if (original.getMarketItem() != null) {
                ptzt.f1Hub.domain.models.copy.market.MarketItem copyItem = new ptzt.f1Hub.domain.models.copy.market.MarketItem();
                copyItem.setId(original.getMarketItem().getId());
                copy.setMarketItem(copyItem);
            }

            if (original.getLeague() != null) {
                ptzt.f1Hub.domain.models.copy.League copyLeague = new ptzt.f1Hub.domain.models.copy.League();
                copyLeague.setId(original.getLeague().getId());
                copy.setLeague(copyLeague);
            }

            return copy;
        };
    }


    @Bean
    public JdbcCursorItemReader<Account> readerAccount(@Qualifier("primaryDatasource") DataSource dataSource) {
        JdbcCursorItemReader<Account> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT * FROM account");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Account.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Account> writerAccount(@Qualifier("secondaryDatasource") DataSource dataSource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Account> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("MERGE INTO account(id, password, email, username, active) VALUES (?, ?, ?, ?, ?)");
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


    @Bean
    public JdbcCursorItemReader<AppUser> readerAppUser(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<AppUser> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM app_user");
        reader.setRowMapper(new BeanPropertyRowMapper<>(AppUser.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AppUser> writerAppUser(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AppUser> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO app_user VALUES (?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getAccount() != null ? item.getAccount().getId() : null);
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


    @Bean
    public JdbcCursorItemReader<AuctionableEntity> readerAuctionableEntity(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<AuctionableEntity> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM auctionable_entity");
        reader.setRowMapper(new BeanPropertyRowMapper<>(AuctionableEntity.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AuctionableEntity> writerAuctionableEntity(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AuctionableEntity> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO auctionable_entity VALUES (?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getNationality());
            ps.setLong(3, item.getPrice());
        });
        return writer;
    }

    @Bean
    public Step stepAuctionableEntity(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                      JdbcCursorItemReader<AuctionableEntity> readerAuctionableEntity,
                                      ItemProcessor<AuctionableEntity, ptzt.f1Hub.domain.models.copy.AuctionableEntity> processorAuctionableEntity,
                                      JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.AuctionableEntity> writerAuctionableEntity) {
        return new StepBuilder("stepAuctionableEntity", jobRepository)
                .<AuctionableEntity, ptzt.f1Hub.domain.models.copy.AuctionableEntity>chunk(100, transactionManager)
                .reader(readerAuctionableEntity)
                .processor(processorAuctionableEntity)
                .writer(writerAuctionableEntity)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Driver> readerDriver(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<Driver> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM driver");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Driver.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Driver> writerDriver(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Driver> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO driver(id, name, nationality, price, points, previous_points, active, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getNationality());
            ps.setLong(4, item.getPrice());
            ps.setLong(5, item.getPoints());
            ps.setLong(6, item.getPreviousPoints());
            ps.setBoolean(7, item.getActive());
            ps.setString(8, item.getImageUrl());
        });
        return writer;
    }

    @Bean
    public Step stepDriver(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                           JdbcCursorItemReader<Driver> readerDriver,
                           ItemProcessor<Driver, ptzt.f1Hub.domain.models.copy.Driver> processorDriverToDriver,
                           JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Driver> writerDriver) {
        return new StepBuilder("stepDriver", jobRepository)
                .<Driver, ptzt.f1Hub.domain.models.copy.Driver>chunk(100, transactionManager)
                .reader(readerDriver)
                .processor(processorDriverToDriver)
                .writer(writerDriver)
                .build();
    }


    @Bean
    public JdbcCursorItemReader<League> readerLeague(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<League> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM league");
        reader.setRowMapper(new BeanPropertyRowMapper<>(League.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.League> writerLeague(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.League> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO league VALUES (?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getName());
            ps.setLong(3, item.getMarket() != null ? item.getMarket().getId() : null);
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


    @Bean
    public JdbcCursorItemReader<Team> readerTeam(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<Team> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM team");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Team.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Team> writerTeam(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Team> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO team(id, name, nationality, price, points, previous_points, active, image_url, line_up_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getNationality());
            ps.setLong(4, item.getPrice());
            ps.setLong(5, item.getPoints());
            ps.setLong(6, item.getPreviousPoints());
            ps.setBoolean(7, item.getActive());
            ps.setString(8, item.getImageUrl());
            ps.setObject(9, item.getLineUp() != null ? item.getLineUp().getId() : null);
        });
        return writer;
    }
    @Bean
    public Step stepTeam(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                         JdbcCursorItemReader<Team> readerTeam,
                         ItemProcessor<Team, ptzt.f1Hub.domain.models.copy.Team> processorTeamToTeam,
                         JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Team> writerTeam) {
        return new StepBuilder("stepTeam", jobRepository)
                .<Team, ptzt.f1Hub.domain.models.copy.Team>chunk(100, transactionManager)
                .reader(readerTeam)
                .processor(processorTeamToTeam)
                .writer(writerTeam)
                .build();
    }


    @Bean
    public JdbcCursorItemReader<Market> readerMarket(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<Market> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM market");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Market.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Market> writerMarket(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Market> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO market VALUES (?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getLeague() != null ? item.getLeague().getId() : null);
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


    @Bean
    public JdbcCursorItemReader<Budget> readerBudget(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<Budget> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM budget");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Budget.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Budget> writerBudget(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.Budget> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO budget VALUES (?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getAppUser() != null ? item.getAppUser().getId() : null);
            ps.setLong(3, item.getLeague() != null ? item.getLeague().getId() : null);
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


    @Bean
    public JdbcCursorItemReader<LineUp> readerLineUp(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<LineUp> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM lineUp");
        reader.setRowMapper(new BeanPropertyRowMapper<>(LineUp.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.LineUp> writerLineUp(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.LineUp> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO lineUp VALUES (?, ?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getAppUser() != null ? item.getAppUser().getId() : null);
            ps.setLong(3, item.getLeague() != null ? item.getLeague().getId() : null);
            ps.setLong(4, item.getTeam() != null ? item.getTeam().getId() : null);
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


    @Bean
    public JdbcCursorItemReader<VerificationToken> readerVerificationToken(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<VerificationToken> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM verification_token");
        reader.setRowMapper(new BeanPropertyRowMapper<>(VerificationToken.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.VerificationToken> writerVerificationToken(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.VerificationToken> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO verification_token VALUES (?, ?, ?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getToken());
            ps.setTimestamp(3, Timestamp.valueOf(item.getExpiresAt()));
            ps.setTimestamp(4, Timestamp.valueOf(item.getConfirmedAt()));
            ps.setLong(5, item.getAccount() != null ? item.getAccount().getId() : null);
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


    @Bean
    public JdbcCursorItemReader<MarketItem> readerMarketItem(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<MarketItem> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM market_item");
        reader.setRowMapper(new BeanPropertyRowMapper<>(MarketItem.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.MarketItem> writerMarketItem(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.MarketItem> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO market_item VALUES (?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getAuctionableEntity() != null ? item.getAuctionableEntity().getId() : null);
        });
        return writer;
    }

    @Bean
    public Step stepMarketItem(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               JdbcCursorItemReader<MarketItem> readerMarketItem,
                               ItemProcessor<MarketItem, ptzt.f1Hub.domain.models.copy.market.MarketItem> processorMarketItemToMarketItem,
                               JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.MarketItem> writerMarketItem) {
        return new StepBuilder("stepMarketItem", jobRepository)
                .<MarketItem, ptzt.f1Hub.domain.models.copy.market.MarketItem>chunk(100, transactionManager)
                .reader(readerMarketItem)
                .processor(processorMarketItemToMarketItem)
                .writer(writerMarketItem)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Offer> readerOffer(@Qualifier("primaryDatasource") DataSource datasource) {
        JdbcCursorItemReader<Offer> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(datasource);
        reader.setSql("SELECT * FROM offer");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Offer.class));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Offer> writerOffer(@Qualifier("secondaryDatasource") DataSource datasource) {
        JdbcBatchItemWriter<ptzt.f1Hub.domain.models.copy.market.Offer> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(datasource);
        writer.setSql("MERGE INTO offer VALUES (?, ?, ?, ?, ?, ?)");
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getAppUser() != null ? item.getAppUser().getId() : null);
            ps.setLong(3, item.getAmount());
            ps.setLong(4, item.getMarketItem() != null ? item.getMarketItem().getId() : null);
            ps.setTimestamp(5, Timestamp.valueOf(item.getCreatedAt()));
            ps.setLong(6, item.getLeague() != null ? item.getLeague().getId() : null);
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

    @Bean
    public Job copyEntitiesJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                               Step stepAccount,
                               Step stepAppUser,
                               Step stepVerificationToken,
                               Step stepLeague,
                               Step stepMarket,
                               Step stepAuctionableEntity,
                               Step stepDriver,
                               Step stepTeam,
                               Step stepLineUp,
                               Step stepBudget,
                               Step stepMarketItem,
                               Step stepOffer) {
        return new JobBuilder("copyEntitiesJob", jobRepository)
                .start(stepAccount)
                .next(stepAppUser)
                .next(stepVerificationToken)
                .next(stepLeague)
                .next(stepMarket)
                .next(stepAuctionableEntity)
                .next(stepDriver)
                .next(stepTeam)
                .next(stepLineUp)
                .next(stepBudget)
                .next(stepMarketItem)
                .next(stepOffer)
                .build();
    }
}