import com.codeborne.selenide.SelenideElement;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class AuthTest {

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
        // сам запрос
        requestSpec.given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем 
                .body(gson.toJson(new RegistrationDto("vasya", "123", "active"))) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда" 
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    @Test
    void shouldAuthorization() {

        open("http://localhost:9999/");
        SelenideElement form = $("form");
        form.$("[data-test-id=login] input").setValue("vasya");
        form.$("[data-test-id=password] input").setValue("123");
        form.$("button.button").click();
        $("h2").shouldHave(exactText("Личный кабинет"));

    }
}

