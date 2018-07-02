package com.ctrlaltelite.copshop.persistence.stubs.hsqldb;

import com.ctrlaltelite.copshop.application.CopShopHub;
import com.ctrlaltelite.copshop.logic.services.utilities.DateUtility;
import com.ctrlaltelite.copshop.objects.ListingObject;
import com.ctrlaltelite.copshop.persistence.IListingModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ListingModelHSQLDB implements IListingModel {
    private static String TABLE_NAME = "LISTINGS";
    private Connection dbConn;

    public ListingModelHSQLDB(final String dbPath) {
        this.dbConn = HSQLDBUtil.getConnection(dbPath);
    }

    @Override
    public void finalize() {
        HSQLDBUtil.closeConnection(dbConn);
    }

    @Override
    public String createNew(ListingObject newListing) {
        if (null == newListing) { throw new IllegalArgumentException("newListing cannot be null"); }

        PreparedStatement st = null;
        ResultSet generatedKeys = null;

        try {
            st = dbConn.prepareStatement(
                    "INSERT INTO " + TABLE_NAME + " " +
                        "(title,description,initprice,minbid,auctionstartdate,auctionenddate,sellerid) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS);
            st.setString(1, newListing.getTitle());
            st.setString(2, newListing.getDescription());
            st.setString(3, newListing.getInitPrice());
            st.setString(4, newListing.getMinBid());
            st.setString(5, newListing.getAuctionStartDate());
            st.setString(6, newListing.getAuctionEndDate());
            st.setInt(7, Integer.parseInt(newListing.getSellerId()));
            int updated = st.executeUpdate();

            if (updated >= 1) {
                generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return String.valueOf(newId);
                }
                generatedKeys.close();
            }
            return null;

        } catch (final SQLException e) {
            e.printStackTrace();
            return null;

        } finally {
            HSQLDBUtil.quietlyClose(generatedKeys);
            HSQLDBUtil.quietlyClose(st);
        }
    }

    @Override
    public boolean delete(String id) {
        if (null == id) { throw new IllegalArgumentException("id cannot be null"); }

        PreparedStatement st = null;

        try {
            st = dbConn.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE id = ?");
            st.setInt(1, Integer.parseInt(id));
            int affected = st.executeUpdate();

            return affected > 0;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            HSQLDBUtil.quietlyClose(st);
        }
    }

    @Override
    public boolean update(String id, ListingObject updatedListing) {
        if (null == id) { throw new IllegalArgumentException("id cannot be null"); }
        if (null == updatedListing) { throw new IllegalArgumentException("updatedListing cannot be null"); }

        PreparedStatement st = null;

        try {
            st = dbConn.prepareStatement(
                    "UPDATE " + TABLE_NAME + " SET " +
                        "title = ?, " +
                        "description = ?, " +
                        "initprice = ?, " +
                        "minbid = ?, " +
                        "auctionstartdate = ?, " +
                        "auctionenddate = ?, " +
                        "sellerid = ? " +
                        "WHERE id = ?");
            st.setString(1, updatedListing.getTitle());
            st.setString(2, updatedListing.getDescription());
            st.setString(3, updatedListing.getInitPrice());
            st.setString(4, updatedListing.getMinBid());
            st.setString(5, updatedListing.getAuctionStartDate());
            st.setString(6, updatedListing.getAuctionEndDate());
            st.setInt(7, Integer.parseInt(updatedListing.getSellerId()));
            st.setInt(8, Integer.parseInt(id));
            st.executeUpdate();
            return true;

        } catch (final SQLException e) {
            e.printStackTrace();
            return false;

        } finally {
            HSQLDBUtil.quietlyClose(st);
        }
    }

    @Override
    public ListingObject fetch(String id) {
        if (null == id) { throw new IllegalArgumentException("id cannot be null"); }

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = dbConn.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE id = ?");
            st.setInt(1, Integer.parseInt(id));
            rs = st.executeQuery();

            ListingObject listingObject = null;
            if (rs.next()) {
                listingObject = fromResultSet(rs);
            }
            return listingObject;

        } catch (final SQLException e) {
            e.printStackTrace();
            return null;

        } finally {
            HSQLDBUtil.quietlyClose(rs);
            HSQLDBUtil.quietlyClose(st);
        }
    }

    @Override
    public List<ListingObject> fetchByName(String name) {
        if (null == name) { throw new IllegalArgumentException("name cannot be null"); }

        List<ListingObject> results = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = dbConn.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE name = ?");
            st.setInt(1, Integer.parseInt(name));
            rs = st.executeQuery();

            while (rs.next()) {
                ListingObject listingObject = fromResultSet(rs);
                results.add(listingObject);
            }

        } catch (final SQLException e) {
            e.printStackTrace();

        } finally {
            HSQLDBUtil.quietlyClose(rs);
            HSQLDBUtil.quietlyClose(st);
        }

        return results;
    }

    @Override
    public List<ListingObject> fetchByLocation(String location) {

        if (null == location) { throw new IllegalArgumentException("location cannot be null"); }

        List<ListingObject> results = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        // get seller id
        String sellerID = CopShopHub.getSellerModel().getSellerID(location);

        try {

            st = dbConn.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE sellerid = ?");
            st.setInt(1, Integer.parseInt(sellerID));
            rs = st.executeQuery();

            while (rs.next()) {
                ListingObject listingObject = fromResultSet(rs);
                results.add(listingObject);
            }

        } catch (final SQLException e) {
            e.printStackTrace();

        } finally {
            HSQLDBUtil.quietlyClose(rs);
            HSQLDBUtil.quietlyClose(st);
        }

        return results;
    }

    @Override
    public List<ListingObject> fetchByCategory(String category) {
        if (null == category) { throw new IllegalArgumentException("category cannot be null"); }

        List<ListingObject> results = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = dbConn.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE category = ?");
            st.setInt(1, Integer.parseInt(category));
            rs = st.executeQuery();

            while (rs.next()) {
                ListingObject listingObject = fromResultSet(rs);
                results.add(listingObject);
            }

        } catch (final SQLException e) {
            e.printStackTrace();

        } finally {
            HSQLDBUtil.quietlyClose(rs);
            HSQLDBUtil.quietlyClose(st);
        }

        return results;
    }

    @Override
    public List<ListingObject> fetchByStatus(String status) {
        if (null == status) { throw new IllegalArgumentException("status cannot be null"); }

        if (status.compareToIgnoreCase("Active") != 0 && status.compareToIgnoreCase("Inactive") != 0 && !status.isEmpty()) {
            throw new IllegalArgumentException("invalid status");
        }

        List<ListingObject> results = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            if(!status.isEmpty()) {
                // get all listings
                st = dbConn.prepareStatement("SELECT * FROM " + TABLE_NAME);
                rs = st.executeQuery();

                Calendar startCal;
                Calendar endCal;

                if (status.compareToIgnoreCase("Active") == 0) {
                    // figure out which listings are active and add to results
                    while (rs.next()) {
                        ListingObject listingObject = fromResultSet(rs);
                        startCal = DateUtility.convertToDateObj(listingObject.getAuctionStartDate());
                        endCal = DateUtility.convertToDateObj(listingObject.getAuctionEndDate());

                        if (startCal.before(Calendar.getInstance(Locale.CANADA)) && endCal.after(Calendar.getInstance(Locale.CANADA))) {
                            results.add(listingObject);
                        }
                    }
                } else {
                    // figure out which listings are inactive and add to results
                    while (rs.next()) {
                        ListingObject listingObject = fromResultSet(rs);
                        startCal = DateUtility.convertToDateObj(listingObject.getAuctionStartDate());
                        endCal = DateUtility.convertToDateObj(listingObject.getAuctionEndDate());

                        if (startCal.after(Calendar.getInstance(Locale.CANADA)) || endCal.before(Calendar.getInstance(Locale.CANADA))) {
                            results.add(listingObject);
                        }
                    }
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();

        } finally {
            HSQLDBUtil.quietlyClose(rs);
            HSQLDBUtil.quietlyClose(st);
        }

        return results;
    }

    @Override
    public List<ListingObject> fetchByFilters(String name, String location, String category, String status) {

        if (null == name) { throw new IllegalArgumentException("name cannot be null"); }

        if (null == location) { throw new IllegalArgumentException("location cannot be null"); }

        if (null == category) { throw new IllegalArgumentException("category cannot be null"); }

        if (null == status) { throw new IllegalArgumentException("status cannot be null"); }

        if (status.compareToIgnoreCase("Active") != 0 && status.compareToIgnoreCase("Inactive") != 0 && !status.isEmpty()) {
            throw new IllegalArgumentException("invalid status");
        }

        List<ListingObject> results = new ArrayList<>();
        List<ListingObject> resultsName = fetchByName(name);
        List<ListingObject> resultsLocation = fetchByLocation(location);
        List<ListingObject> resultsCategory = fetchByCategory(category);
        List<ListingObject> resultsStatus = fetchByStatus(status);

        // add listings with name provided as a parameter
        for (ListingObject listing : resultsName){
            if (!results.contains(listing))
                results.add(listing);
        }

        // add listings with location provided as a parameter
        for (ListingObject listing : resultsLocation){
            if (!results.contains(listing))
                results.add(listing);
        }

        // add listings with category provided as a parameter
        for (ListingObject listing : resultsCategory){
            if (!results.contains(listing))
                results.add(listing);
        }

        // add listings with status provided as a parameter
        for (ListingObject listing : resultsStatus){
            if (!results.contains(listing))
                results.add(listing);
        }

        return results;

        /*
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = dbConn.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE name = ? AND location = ? AND category = ? AND status = ?");
            st.setInt(1, Integer.parseInt(name));
            st.setInt(2, Integer.parseInt(location));
            st.setInt(3, Integer.parseInt(category));
            st.setInt(4, Integer.parseInt(status));
            rs = st.executeQuery();

            while (rs.next()) {
                ListingObject listingObject = fromResultSet(rs);
                results.add(listingObject);
            }

        } catch (final SQLException e) {
            e.printStackTrace();

        } finally {
            HSQLDBUtil.quietlyClose(rs);
            HSQLDBUtil.quietlyClose(st);
        }

        return results;
        */
    }

    @Override
    public List<ListingObject> fetchAll() {
        List<ListingObject> results = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = dbConn.prepareStatement("SELECT * FROM " + TABLE_NAME);
            rs = st.executeQuery();

            while (rs.next()) {
                ListingObject listingObject = fromResultSet(rs);
                results.add(listingObject);
            }

        } catch (final SQLException e) {
            e.printStackTrace();

        } finally {
            HSQLDBUtil.quietlyClose(rs);
            HSQLDBUtil.quietlyClose(st);
        }

        return results;
    }

    private ListingObject fromResultSet(final ResultSet rs) throws SQLException {
        if (null == rs) { throw new IllegalArgumentException("resultSet cannot be null"); }

        String title = HSQLDBUtil.getStringFromResultSet(rs, "title");
        String desc = HSQLDBUtil.getStringFromResultSet(rs, "description");
        String initPrice = HSQLDBUtil.getStringFromResultSet(rs, "initprice");
        String minBid = HSQLDBUtil.getStringFromResultSet(rs, "minbid");
        String startDate = HSQLDBUtil.getStringFromResultSet(rs, "auctionstartdate");
        String endDate = HSQLDBUtil.getStringFromResultSet(rs, "auctionenddate");
        String sellerId = HSQLDBUtil.getIntAsStringFromResultSet(rs, "sellerid");
        String id = HSQLDBUtil.getIntAsStringFromResultSet(rs, "id");

        //System.out.println("Created Listing Object: " + id + ", " + title + ", " + desc + ", " + initPrice + ", " + minBid + ", " + startDate + ", " + endDate + ", " + sellerId);
        return new ListingObject(id, title, desc, initPrice, minBid, startDate, endDate, sellerId);
    }
}
