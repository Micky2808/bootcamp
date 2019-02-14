package com.bootcamp.core.content;

import com.day.commons.datasource.poolservice.DataSourcePool;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Component(immediate = true)
@Service(CustomerService.class)
public class CustomerServiceImpl implements CustomerService {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Reference
    private transient DataSourcePool dataSourcePool;

    private Connection getConnection() {
        DataSource dataSource = null;
        Connection connection = null;
        try {
            //Inject the DataSourcePool right here!
            dataSource = (DataSource) dataSourcePool.getDataSource("root");
            if (dataSource != null) {
                connection = dataSource.getConnection();
            }
            return connection;

        } catch (Exception e) {
            LOG.error("Could not connect to database {}", e);
        }
        return null;

    }

    @Override
    public void insertCustData() {
        int customerId = 1, zipCode = 560086;
        String name = "mahesh", email = "mahesh@gmail.com", shippingAddress = "bangalore", state = "Ka";

        try (Connection connection = getConnection()) {
            String query = "insert into forms.customer_data values(?,?,?,?,?,?,?)";
            if (connection != null) ;
            {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, customerId);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, shippingAddress);
                    preparedStatement.setString(5, state);
                    preparedStatement.setInt(6, zipCode);
                    boolean execute = preparedStatement.execute();
                    if (!execute) {
                        LOG.info("Row inserted successfully");
                    }
                } catch (Exception e) {
                    LOG.error("prepared statement could not be executed {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("Connection could not be established {}", e.getMessage());
        }
    }

    @Override
    public String getCustomerData(String filter) {
        return null;
    }
}