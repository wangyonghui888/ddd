/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.tickets;

import com.sportradar.mts.sdk.api.Sender;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.enums.*;

/**
 * Examples how to build tickets from the MTS Ticket Integration guide (V31), chapter 10
 */
public class TicketExamples
{
    private BuilderFactory builderFactory;

    public TicketExamples(BuilderFactory builderFactory)
    {
        this.builderFactory = builderFactory;
    }

    private Sender GetSender()
    {
        return builderFactory.createSenderBuilder()
                //.setBookmakerId(1)
                //.setLimitId(1)
                .setSenderChannel(SenderChannel.INTERNET)
                .setCurrency("EUR")
                .setEndCustomer("1.2.3.4", "Customer-" + System.nanoTime(), "EN", "deviceId-123", 10000L)
                .build();
    }

    /**
     * 10.1 Ticket with Live single bet with 3-Way market
     * Ticket with single bet on Live soccer event (Event ID: 11057047), 3way, Away team (TypeID: 2; SubTypeID: 0; Special Odds value: *; Selection: 2)
     * @return Ticket
     */
    public Ticket Example1()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example1-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("19427908")
                                .setIdLo(1, 1, "", "2")
                                .setOdds(12100)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.2 Pre-match Single bet Ticket
     * Ticket with single bet on Pre-match Soccer(Sport ID: 1) event (Event ID: 11050343), Halftime - 3way, Draw(oddstype: 42, Special Odds value: *; Selection: X)
     * @return Ticket
     */
    public Ticket Example2()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example2-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050343")
                                .setIdLcoo(42, 1, "", "X")
                                .setOdds(28700)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.3 Double Pre-match
     * Ticket with double bet on two pre-match selections
     * Soccer (Sport ID: 1), Event ID: 11050343, Halftime - 3way (oddstype: 42), Special Odds value: *; Draw   
     * Soccer (Sport ID: 1), Event ID: 10784408, Asian handicap first half (oddstype: 53), Special Odds value: 0.25; Home
     * @return Ticket
     */
    public Ticket Example3()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example3-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(2)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050343")
                                .setIdLcoo(42, 1, "", "X")
                                .setOdds(28700)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("10784408")
                                .setIdLcoo(53, 1, "0.25", "1")
                                .setOdds(16600)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.4 Treble on Pre-match and Live
     * @return Ticket
     */
    public Ticket Example4()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example4-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(3)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050343")
                                .setIdLcoo(42, 1, "", "1")
                                .setOdds(39600)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("10784408")
                                .setIdLcoo(42, 1, "", "1")
                                .setOdds(36600)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052925")
                                .setIdLo(8, 518, "1-3", "NO")
                                .setOdds(13799)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.5 System 3 / 4
     * @return Ticket
     */
    public Ticket Example5()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example5-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(3)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050343")
                                .setIdLcoo(42, 1, "", "1")
                                .setOdds(28700)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("10784408")
                                .setIdLcoo(53, 1, "-0.25", "2")
                                .setOdds(14800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11046885")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(11299)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "1")
                                .setOdds(23500)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.6 SYSTEM 3 / 4 including 1 Banker
     * @return Ticket
     */
    public Ticket Example6()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example6-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(3)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050343")
                                .setIdLcoo(42, 1, "", "1")
                                .setOdds(28700)
                                .setBanker(true)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("10784408")
                                .setIdLcoo(53, 1, "-0.25", "2")
                                .setOdds(14800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11046885")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(11299)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "1")
                                .setOdds(23500)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.7 System 3 / 5 incl 1 Ways
     * @return Ticket
     */
    public Ticket Example7()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example7-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(3)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050343")
                                .setIdLcoo(42, 1, "", "X")
                                .setOdds(28700)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("10784408")
                                .setIdLcoo(53, 1, "-0.25", "2")
                                .setOdds(14800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11046885")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(11299)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "1")
                                .setOdds(23500)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.8 System 3 / 5 including 1 Ways including 1 banker
     * @return Ticket
     */
    public Ticket Example8()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example8-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(3)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050343")
                                .setIdLcoo(42, 1, "", "X")
                                .setOdds(28700)
                                .setBanker(true)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("10784408")
                                .setIdLcoo(53, 1, "-0.25", "2")
                                .setOdds(14800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11046885")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(11299)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "1")
                                .setOdds(23500)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.9 Championship Outright
     * @return Ticket
     */
    public Ticket Example9()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example9-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("40777")
                                .setIdLcoo(30, 14, "", "6495408")
                                .setOdds(12200)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.10 Podium Outright
     * @return Ticket
     */
    public Ticket Example10()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example10-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("40987")
                                .setIdLcoo(50, 14, "", "7080578")
                                .setOdds(121600)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.11 Two single bets on the same event within one ticket
     * Ticket example where punter/better/endCustomer choose two selections from different markets, but from the same event
     * @return Ticket
     */
    public Ticket Example11()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example11-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B1-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050841")
                                .setIdLcoo(10, 1, "", "1")
                                .setOdds(17700)
                                .build())
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B2-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050841")
                                .setIdLcoo(51, 1, "-1.25", "2")
                                .setOdds(15600)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.12 4-Fold Accumulator with 80 Cents Sport-betting bonus
     * @return Ticket
     */
    public Ticket Example12()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example12-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B-" + System.nanoTime())
                        .setStake(10000, StakeType.TOTAL)
                        .addSelectedSystem(4)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050915")
                                .setIdLcoo(20, 5, "", "1")
                                .setOdds(14100)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(13600)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052893")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(16900)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11051699")
                                .setIdLcoo(20, 5, "", "1")
                                .setOdds(10900)
                                .build())
                        .setBetBonus(8000, BetBonusMode.ALL, BetBonusType.TOTAL)
                        .build())
                .build();
    }

    /**
     * 10.13 Multi-system bets ticket with different stakes
     * @return Ticket
     */
    public Ticket Example13()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example13-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B1-" + System.nanoTime())
                        .setStake(40000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050915")
                                .setIdLcoo(20, 5, "", "1")
                                .setOdds(14100)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052893")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(16900)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052537")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(10400)
                                .build())
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B2-" + System.nanoTime())
                        .setStake(60000, StakeType.TOTAL)
                        .addSelectedSystem(2)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050915")
                                .setIdLcoo(20, 5, "", "1")
                                .setOdds(14100)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052893")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(16900)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052537")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(10400)
                                .build())
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B3-" + System.nanoTime())
                        .setStake(120000, StakeType.TOTAL)
                        .addSelectedSystem(3)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050915")
                                .setIdLcoo(20, 5, "", "1")
                                .setOdds(14100)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052893")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(16900)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052537")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(10400)
                                .build())
                        .build())
                .build();
    }

    /**
     * 10.14 Multi-systems ticket with different number of selections and with different unit-stakes
     * @return Ticket
     */
    public Ticket Example14()
    {
        return builderFactory.createTicketBuilder()
                .setTicketId("Example14-" + System.nanoTime())
                .setSender(GetSender())
                .setOddsChange(OddsChangeType.ANY)
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B1-" + System.nanoTime())
                        .setStake(60000, StakeType.TOTAL)
                        .addSelectedSystem(3)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11050915")
                                .setIdLcoo(20, 5, "", "1")
                                .setOdds(14100)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052893")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(16900)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052531")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(15600)
                                .build())
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B2-" + System.nanoTime())
                        .setStake(120000, StakeType.TOTAL)
                        .addSelectedSystem(2)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052893")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(16900)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052531")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(15600)
                                .build())
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId("B3-" + System.nanoTime())
                        .setStake(80000, StakeType.TOTAL)
                        .addSelectedSystem(1)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11029671")
                                .setIdLcoo(339, 5, "1.5", "2")
                                .setOdds(16800)
                                .build())
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("11052531")
                                .setIdLcoo(20, 5, "", "2")
                                .setOdds(15600)
                                .build())
                        .build())
                .build();
    }
}
