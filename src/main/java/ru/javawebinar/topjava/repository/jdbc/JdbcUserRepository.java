package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final ResultSetExtractorMapper resultSetExtractorMapper;

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.resultSetExtractorMapper = new ResultSetExtractorMapper();
    }

    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        } else {
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
        }

        List<Role> roles = new ArrayList<>(user.getRoles());
        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, user.getId());
                        preparedStatement.setString(2, roles.get(i).name());
                    }

                    @Override
                    public int getBatchSize() {
                        return roles.size();
                    }
                });

        return user;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT users.*, ur.role AS roles FROM users LEFT JOIN user_roles ur ON users.id = ur.user_id WHERE id=?",
                resultSetExtractorMapper,
                id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query(
                "SELECT users.*, ur.role AS roles FROM users LEFT JOIN user_roles ur ON users.id = ur.user_id WHERE email=?",
                resultSetExtractorMapper,
                email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "SELECT users.*, ur.role AS roles FROM users LEFT JOIN user_roles ur ON users.id = ur.user_id ORDER BY name, email",
                resultSetExtractorMapper);
    }

    private static class ResultSetExtractorMapper implements ResultSetExtractor<List<User>> {

        @Override
        public List<User> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Integer, User> data = new LinkedHashMap<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Role role = Role.valueOf(resultSet.getString("roles"));
                if (data.containsKey(id)) {
                    data.get(id).getRoles().add(role);
                } else {
                    User user = new User();
                    user.setId(id);
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setRegistered(resultSet.getDate("registered"));
                    user.setCaloriesPerDay(resultSet.getInt("calories_per_day"));
                    user.setRoles(Collections.singleton(role));
                    data.put(id, user);
                }
            }
            return new ArrayList<>(data.values());
        }
    }
}
