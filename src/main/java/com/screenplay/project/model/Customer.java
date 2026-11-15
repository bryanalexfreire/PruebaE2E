package com.screenplay.project.model;

/**
 * Data typed into the "Place order" form. Test data only (fictitious names and card numbers).
 *
 * @param name    customer name
 * @param country country
 * @param city    city
 * @param card    credit card number
 * @param month   card expiry month
 * @param year    card expiry year
 */
public record Customer(String name, String country, String city, String card, String month, String year) {
}
