package com.example.jdbc.project.dao;

import com.example.jdbc.project.entity.Flight;
import com.example.jdbc.project.exception.DaoException;
import com.example.jdbc.project.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight> {
    private static final FlightDao INSTANCE = new FlightDao();
    private static final String CREATE_SQL = "INSERT INTO flight(" +
            "flight_no," +
            " departure_date," +
            " departure_airport_code," +
            " arrival_date," +
            " arrival_airport_code," +
            " aircraft_id," +
            " status) " +
            "VALUES(?,?,?,?,?,?,?) ";
    private static final String FIND_BY_ID_SQL = "SELECT " +
            "id," +
            "flight_no," +
            "departure_date," +
            "departure_airport_code," +
            "arrival_date," +
            "arrival_airport_code," +
            "aircraft_id," +
            "status FROM flight WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT " +
            "id," +
            "flight_no," +
            "departure_date," +
            "departure_airport_code," +
            "arrival_date," +
            "arrival_airport_code," +
            "aircraft_id," +
            "status FROM flight";
    private static final String UPDATE_SQL = "UPDATE flight SET" +
            " flight_no= ?," +
            "departure_date = ?," +
            "departure_airport_code = ?," +
            "arrival_date = ?," +
            "arrival_airport_code = ?," +
            "aircraft_id = ?," +
            "status = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM flight WHERE id = ?";

    private FlightDao() {
    }

    public Flight create(Flight flight) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, flight.getFlightNo());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureDate()));
            preparedStatement.setString(3, flight.getDepartureAirportCode());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(flight.getArrivalDate()));
            preparedStatement.setString(5, flight.getArrivalAirportCode());
            preparedStatement.setInt(6, flight.getAircraftId());
            preparedStatement.setString(7, flight.getStatus());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                flight.setId(generatedKeys.getLong("id"));
            }
            return flight;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Flight> findById(Long id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Flight flight = null;
            if (resultSet.next()) {
                flight = getBuild(resultSet);
            }
            return Optional.ofNullable(flight);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    public List<Flight> findAll() {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Flight> flightList = new ArrayList<>();
            while (resultSet.next()) {
                flightList.add(getBuild(resultSet));
            }
            return flightList;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void update(Flight flight) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, flight.getFlightNo());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureDate()));
            preparedStatement.setString(3, flight.getDepartureAirportCode());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(flight.getArrivalDate()));
            preparedStatement.setString(5, flight.getArrivalAirportCode());
            preparedStatement.setInt(6, flight.getAircraftId());
            preparedStatement.setString(7, flight.getStatus());
            preparedStatement.setLong(8, flight.getId());
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

    private Flight getBuild(ResultSet resultSet) throws SQLException {
        return Flight.builder()
                .id(resultSet.getLong("id"))
                .flightNo(resultSet.getString("flight_no"))
                .departureDate(resultSet.getTimestamp("departure_date").toLocalDateTime())
                .departureAirportCode(resultSet.getString("departure_airport_code"))
                .arrivalDate(resultSet.getTimestamp("arrival_date").toLocalDateTime())
                .arrivalAirportCode(resultSet.getString("arrival_airport_code"))
                .aircraftId(resultSet.getInt("aircraft_id"))
                .status(resultSet.getString("status"))
                .build();

    }


    public static FlightDao getInstance() {
        return INSTANCE;
    }

}
