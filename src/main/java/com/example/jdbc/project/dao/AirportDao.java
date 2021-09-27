package com.example.jdbc.project.dao;

import com.example.jdbc.project.entity.Airport;
import com.example.jdbc.project.exception.DaoException;
import com.example.jdbc.project.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirportDao implements Dao<String, Airport> {
    private static final AirportDao INSTANCE = new AirportDao();
    private static final String CREATE_SQL = "INSERT INTO airport(code, country, city) VALUES (?,?,?)";
    private static final String FIND_BY_ID_SQL = "SELECT code,country,city FROM airport WHERE code = ?";
    private static final String FIND_ALL_SQL = "SELECT code,country,city FROM airport";
    private static final String UPDATE_SQL = "UPDATE airport SET country = ?,city = ? WHERE code = ?";
    private static final String DELETE_SQL = "DELETE FROM airport WHERE code = ?";

    private AirportDao() {
    }

    @Override
    public Airport create(Airport entity) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL);
            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getCountry());
            preparedStatement.setString(3, entity.getCity());
            preparedStatement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Airport> findById(String id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Airport airport = null;
            if (resultSet.next()) {
                airport = buildAirport(resultSet);
            }
            return Optional.ofNullable(airport);
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public List<Airport> findAll() {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Airport> airportList = new ArrayList<>();
            while (resultSet.next()) {
                airportList.add(buildAirport(resultSet));
            }
            return airportList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Airport entity) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, entity.getCountry());
            preparedStatement.setString(2, entity.getCity());
            preparedStatement.setString(3, entity.getCode());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public boolean delete(String id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);
            preparedStatement.setString(1, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Airport buildAirport(ResultSet resultSet) throws SQLException {
        return Airport.builder().code(resultSet.getString("code"))
                .country(resultSet.getString("country")).city(resultSet.getString("city")).build();
    }

    public static AirportDao getInstance() {
        return INSTANCE;
    }
}
