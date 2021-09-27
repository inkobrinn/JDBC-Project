package com.example.jdbc.project.dao;

import com.example.jdbc.project.dto.TicketFilter;
import com.example.jdbc.project.entity.Ticket;
import com.example.jdbc.project.exception.DaoException;
import com.example.jdbc.project.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();
    private static final String DELETE_SQL = "DELETE FROM ticket WHERE id = ?";
    private static final String CREATE_SQL = "INSERT INTO ticket(" +
            "passenger_no," +
            " passenger_name," +
            " flight_id," +
            " seat_no," +
            " cost)" +
            " VALUES(?,?,?,?,?)";
    private static final String UPDATE_SQL = "UPDATE ticket SET " +
            "passenger_no = ?, " +
            "passenger_name = ?," +
            "flight_id = ?," +
            "seat_no = ?," +
            "cost = ? WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT " +
            "id," +
            "passenger_no," +
            "passenger_name," +
            "flight_id," +
            "seat_no," +
            "cost FROM ticket WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT " +
            "id," +
            "passenger_no," +
            "passenger_name," +
            "flight_id," +
            "seat_no," +
            "cost FROM ticket";

    private TicketDao() {
    }

    public Ticket create(Ticket ticket) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlightId());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                ticket.setId(generatedKeys.getLong("id"));
            }
            return ticket;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Ticket> findById(Long id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Ticket ticket = null;
            if (resultSet.next()) {
                ticket = getTicket(resultSet);

            }
            return Optional.ofNullable(ticket);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Ticket> findAll(TicketFilter ticketFilter) {
        List<Object> parameters = new ArrayList<>();
        parameters.add(ticketFilter.getLimit());
        parameters.add(ticketFilter.getOffset());
        String sql = FIND_ALL_SQL + " LIMIT ? OFFSET ?";
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ticket> ticketList = new ArrayList<>();
            while (resultSet.next()) {
                ticketList.add(getTicket(resultSet));
            }
            return ticketList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Ticket> findAll() {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ticket> ticketList = new ArrayList<>();
            while (resultSet.next()) {
                ticketList.add(getTicket(resultSet));
            }
            return ticketList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void update(Ticket ticket) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlightId());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.setLong(6, ticket.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Ticket getTicket(ResultSet resultSet) throws SQLException {

        return Ticket.builder()
                .id(resultSet.getLong("id"))
                .passengerNo(resultSet.getString("passenger_no"))
                .passengerName(resultSet.getString("passenger_name"))
                .flightId(resultSet.getLong("flight_id"))
                .seatNo(resultSet.getString("seat_no"))
                .cost(resultSet.getBigDecimal("cost"))
                .build();

    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }


}
