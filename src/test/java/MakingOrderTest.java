import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.hamcrest.MatcherAssert;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
@RunWith(Parameterized.class)
public class MakingOrderTest {
    private final OrderCreation creatingOrder;

    public MakingOrderTest(OrderCreation creatingOrder) { this.creatingOrder = creatingOrder; }

    @Parameterized.Parameters
    public static Collection<Object[]> list(){
        return Arrays.asList(new Object[][]{
                {new OrderCreation("Harry", "Potter", "Hogwarts, 23", 7, "+1 222 333 44 55", 10, "2024-06-26", "You are a wizard, Harry", new String[]{"BLACK"})},
                {new OrderCreation("Harry", "Potter", "Hogwarts, 23", 7, "+1 222 333 44 55", 10, "2024-06-26", "You are a wizard, Harry", new String[]{"GREY"})},
                {new OrderCreation("Harry", "Potter", "Hogwarts, 23", 7, "+1 222 333 44 55", 10, "2024-06-26", "You are a wizard, Harry", new String[]{"BLACK", "GREY"})},
                {new OrderCreation("Harry", "Potter", "Hogwarts, 23", 7, "+1 222 333 44 55", 10, "2024-06-26", "You are a wizard, Harry", new String[]{})}
        });
    }
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }
    @Step("Создание заказа")
    private Response creatingOrder(OrderCreation creatingOrder) {
        return given()
                .header("Content-type", "application/json")
                .body(creatingOrder)
                .post("/api/v1/orders")
                .then()
                .extract()
                .response(); }

    @Test
    @Step("Создать заказ")
    public void createAnOrder() {
        Response response = creatingOrder(creatingOrder);
        response.then().statusCode(201);
        Integer track = response.path("track");
        MatcherAssert.assertThat(track, notNullValue());  }

    @Step("Получить список заказов")
    private Response gettingListOrders(){
        return given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders")
                .then()
                .extract()
                .response(); }

}