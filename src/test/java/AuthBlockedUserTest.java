import com.codeborne.selenide.SelenideElement;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class AuthBlockedUserTest {

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeAll
    static void setUpAll() {

        Gson gson = new Gson();

        given()
                .spec(requestSpec)
                .body(gson.toJson(new RegistrationDto("vasya", "123", "blocked")))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldNotAuthorizationValidUser() {

        open("http://localhost:9999/");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("vasya");
        form.$("[data-test-id=password] input").setValue("123");
        form.$("button.button").click();
        $("[data-test-id=error-notification]").waitUntil(visible, 5000);
        $("[data-test-id=error-notification]  .notification__title").shouldHave(exactText("Ошибка"));
        $("[data-test-id=error-notification]  .notification__content").shouldHave(exactText("Ошибка! Пользователь заблокирован"));
    }

    @Test
    void shouldNotAuthorizationNotValidPassword() {

        open("http://localhost:9999/");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("vasya");
        form.$("[data-test-id=password] input").setValue("321");
        form.$("button.button").click();
        $("[data-test-id=error-notification]").waitUntil(visible, 5000);
        $("[data-test-id=error-notification]  .notification__title").shouldHave(exactText("Ошибка"));
        $("[data-test-id=error-notification]  .notification__content").shouldHave(exactText("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldNotAuthorizationNotValidLogin() {

        open("http://localhost:9999/");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("petya");
        form.$("[data-test-id=password] input").setValue("123");
        form.$("button.button").click();
        $("[data-test-id=error-notification]").waitUntil(visible, 5000);
        $("[data-test-id=error-notification]  .notification__title").shouldHave(exactText("Ошибка"));
        $("[data-test-id=error-notification]  .notification__content").shouldHave(exactText("Ошибка! Неверно указан логин или пароль"));
    }
}

