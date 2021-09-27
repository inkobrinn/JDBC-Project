package com.example.jdbc.project.dao;

import com.example.jdbc.project.entity.Aircraft;
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

public class AircraftDao implements Dao<Integer, Aircraft> {
    private static final AircraftDao INSTANCE = new AircraftDao();
    private static final String CREATE_SQL = "INSERT INTO aircraft(model) VALUES (?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, model FROM aircraft WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id,model FROM aircraft";
    private static final String UPDATE_SQL = "UPDATE aircraft SET model = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM aircraft WHERE id = ?";

    private AircraftDao() {

    }

    public Aircraft create(Aircraft aircraft) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, aircraft.getModel());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                aircraft.setId(generatedKeys.getInt("id"));
            }
            return aircraft;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Aircraft> findById(Integer id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Aircraft aircraft = null;
            if (resultSet.next()) {
                aircraft = buildAircraft(resultSet);
            }
            return Optional.ofNullable(aircraft);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Aircraft> findAll() {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Aircraft> aircraftList = new ArrayList<>();
            while (resultSet.next()) {
                aircraftList.add(buildAircraft(resultSet));
            }
            return aircraftList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void update(Aircraft aircraft) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, aircraft.getModel());
            preparedStatement.setInt(2, aircraft.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean delete(Integer id) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Aircraft buildAircraft(ResultSet resultSet) throws SQLException {

        return Aircraft.builder()
                .id(resultSet.getInt("id"))
                .model(resultSet.getString("model"))
                .build();

    }

    public static AircraftDao getInstance() {
        return INSTANCE;
    }
}
