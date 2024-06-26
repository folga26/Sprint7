import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierCreationTest {
    private static final String BASE_URL = "http://qa-scooter.praktikum-services.ru";

    @Before
    public void setUp() {  RestAssured.baseURI = BASE_URL; }

    @Step("1. Создать курьера")
    private void creatingCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier")
                .then()
                .statusCode(201); }

    @Step("2. Авторизоваться под курьером в системе")
    private Response courierLogin(String login, String password) {
        CourierLogin loginCourierInSystem = new CourierLogin(login, password);

        return given()
                .header("Content-type", "application/json")
                .body(loginCourierInSystem)
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();  }

    @Test
    @Description("Успешное создание курьера")
    @Step("Авторизация прошла успешно")
    public void successfulLogin() {
        String random = UUID.randomUUID().toString();
        creatingCourier("folga" + random, "qwer1234", "QATester");

        Response response = courierLogin("folga" + random, "qwer1234");
        response.then().statusCode(200);

        Integer id = response.path("id");
        MatcherAssert.assertThat(id, notNullValue()); }


    @Test
    @DisplayName("Создание курьера без логина или пароля")
    @Description("Вернуть ошибку, если нет какого-либо из полей")
    @Step("Логин не заполнен")
    public void requestWithoutLogin() {
        Response response = courierLogin(null, "qwer1234");
        response.then().statusCode(400);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Недостаточно данных для входа")); }


    @Test
    @Description("Ввести несуществующие логин и пароль")
    @Step("Авторизоваться с  несуществующими логином и паролем")
    public void requestWithNonLoginPassword() {
        String random = UUID.randomUUID().toString();
        creatingCourier("folga" + random, "qwer1234", "QATester");

        Response response = courierLogin("non-existing login", "non-existing password");
        response.then().statusCode(404);

        String message = response.path("message");
        MatcherAssert.assertThat(message, equalTo("Учетная запись не найдена")); }
}