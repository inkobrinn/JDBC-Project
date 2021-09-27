package com.example.jdbc.project.dao;

import com.example.jdbc.project.entity.Seat;
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


public class SeatDao implements Dao<Seat, Seat> {
    private static final SeatDao INSTANCE = new SeatDao();
    private static final String CREATE_SQL = "INSERT INTO seat(aircraft_id, seat_no) VALUES (?, ?)";
    private static final String FIND_ALL_SQL = "SELECT aircraft_id,seat_no FROM seat";
    private static final String FIND_BY_ID_SQL = "SELECT aircraft_id,seat_no FROM seat WHERE aircraft_id = ? and seat_no = ?";
    private static final String UPDATE_SQL = "UPDATE seat SET seat_no = ? WHERE aircraft_id = ? AND seat_no = ?";
    private static final String DELETE_SQL = "DELETE FROM seat WHERE aircraft_id = ? AND seat_no = ?";


    private SeatDao() {
    }

    public Seat create(Seat seat) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, seat.getAircraftId());
            preparedStatement.setString(2, seat.getSeatNo());
            preparedStatement.executeUpdate();

            return seat;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Seat> findById(Seat key) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setInt(1, key.getAircraftId());
            preparedStatement.setString(2, key.getSeatNo());
            ResultSet resultSet = preparedStatement.executeQuery();
            Seat seat = null;
            while (resultSet.next()) {
                seat = getBuild(resultSet);
            }
            return Optional.ofNullable(seat);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    public List<Seat> findAll() {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Seat> seatList = new ArrayList<>();
            while (resultSet.next()) {
                seatList.add(getBuild(resultSet));
            }
            return seatList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public void update(Seat entity) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, entity.getSeatNo());
            preparedStatement.setInt(2, entity.getAircraftId());
            preparedStatement.setString(3, "B1");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public boolean delete(Seat key) {
        try (Connection connection = ConnectionManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);
            preparedStatement.setInt(1, key.getAircraftId());
            preparedStatement.setString(2, key.getSeatNo());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static SeatDao getINSTANCE() {
        return INSTANCE;
    }

    private Seat getBuild(ResultSet resultSet) throws SQLException {
        return Seat.builder().
                aircraftId(resultSet.getInt("aircraft_id"))
                .seatNo(resultSet.getString("seat_no")).build();
    }
}
