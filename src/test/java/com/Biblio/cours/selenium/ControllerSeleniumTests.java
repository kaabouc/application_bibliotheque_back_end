//package com.Biblio.cours.selenium;
//
//import org.junit.jupiter.api.*;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import java.time.Duration;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class ControllerSeleniumTests {
//    private static WebDriver driver;
//    private static WebDriverWait wait;
//    private static final String BASE_URL = "http://localhost:3000";
//    private static String authToken;
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeAll
//    public static void setUp() {
//        WebDriverManager.chromedriver().setup();
//        driver = new ChromeDriver();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Increased wait for React elements
//        driver.manage().window().maximize();
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//    }
//
//    @AfterAll
//    public static void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//
//    @Test
//    @Order(1)
//    public void testUserRegistration() {
//        driver.get(BASE_URL + "/register");
//
//        // Vérifiez si les identifiants des éléments sont corrects
//        try {
//            ObjectNode userData = objectMapper.createObjectNode()
//                    .put("email", "test@example.com")
//                    .put("password", "testPassword123")
//                    .put("confirmPassword", "testPassword123");
//
//            // Attendre que les champs soient interactifs et saisir les valeurs
//            WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("email")));
//            emailInput.sendKeys(userData.get("email").asText());
//            System.out.println("Email saisi avec succès.");
//
//            WebElement passwordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("password")));
//            passwordInput.sendKeys(userData.get("password").asText());
//            System.out.println("Mot de passe saisi avec succès.");
//
//            // Vérifier si le champ confirm-password existe et saisir la valeur
//            WebElement confirmPasswordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirm-password")));
//            confirmPasswordInput.sendKeys(userData.get("confirmPassword").asText());
//            System.out.println("Confirmation du mot de passe saisi avec succès.");
//
//            WebElement submitButton = driver.findElement(By.id("register-submit"));
//            submitButton.click();
//
//            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("success-message")));
//            assertTrue(successMessage.isDisplayed(), "Le message de succès de l'enregistrement devrait s'afficher.");
//        } catch (Exception e) {
//            System.out.println("Une erreur s'est produite lors de la saisie des données : " + e.getMessage());
//        }
//    }
//
//    @Test
//    @Order(2)
//    public void testUserLogin() {
//        driver.get(BASE_URL + "/login");
//
//        try {
//            WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-email")));
//            emailInput.sendKeys("test@example.com");
//            System.out.println("Email de connexion saisi avec succès.");
//
//            WebElement passwordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-password")));
//            passwordInput.sendKeys("testPassword123");
//            System.out.println("Mot de passe de connexion saisi avec succès.");
//
//            WebElement loginButton = driver.findElement(By.id("login-submit"));
//            loginButton.click();
//
//            WebElement tokenElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("jwt-token")));
//            authToken = tokenElement.getText();
//
//            assertNotNull(authToken, "Le token d'authentification ne doit pas être nul.");
//            assertTrue(authToken.length() > 0, "Le token d'authentification ne doit pas être vide.");
//        } catch (Exception e) {
//            System.out.println("Une erreur s'est produite lors de la connexion : " + e.getMessage());
//        }
//    }
//
////
////    @Test
////    @Order(3)
////    public void testDocumentUpload() {
////        driver.get(BASE_URL + "/document/upload");
////
////        ((JavascriptExecutor) driver).executeScript(
////                "localStorage.setItem('authToken', arguments[0])", authToken);
////
////        WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("document-title")));
////        titleInput.sendKeys("Test Document");
////
////        WebElement descriptionInput = driver.findElement(By.id("document-description"));
////        descriptionInput.sendKeys("Test Description");
////
////        WebElement filierInput = driver.findElement(By.id("document-filier"));
////        filierInput.sendKeys("Test Filier");
////
////        WebElement niveauxInput = driver.findElement(By.id("document-niveaux"));
////        niveauxInput.sendKeys("Test Niveau");
////
////        WebElement fileInput = driver.findElement(By.id("document-file"));
////        fileInput.sendKeys(System.getProperty("user.dir") + "/src/test/resources/test-file.pdf");
////
////        WebElement uploadButton = driver.findElement(By.id("document-submit"));
////        uploadButton.click();
////
////        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("upload-success")));
////        assertTrue(successMessage.isDisplayed(), "Document upload success message should be displayed");
////    }
////
////    @Test
////    @Order(4)
////    public void testAdminFunctions() {
////        driver.get(BASE_URL + "/login");
////
////        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-email")));
////        emailInput.sendKeys("admin@example.com");
////
////        WebElement passwordInput = driver.findElement(By.id("login-password"));
////        passwordInput.sendKeys("adminPassword123");
////
////        WebElement loginButton = driver.findElement(By.id("login-submit"));
////        loginButton.click();
////
////        driver.get(BASE_URL + "/admin/users");
////        WebElement usersList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("users-list")));
////        assertTrue(usersList.isDisplayed(), "Admin users list should be visible");
////
////        driver.get(BASE_URL + "/admin/bibliotheque/new");
////        ObjectNode biblioData = objectMapper.createObjectNode()
////                .put("name", "Test Bibliotheque");
////
////        WebElement biblioNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("biblio-name")));
////        biblioNameInput.sendKeys(biblioData.get("name").asText());
////
////        WebElement biblioSubmit = driver.findElement(By.id("biblio-submit"));
////        biblioSubmit.click();
////
////        WebElement biblioSuccess = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("biblio-success")));
////        assertTrue(biblioSuccess.isDisplayed(), "Bibliotheque creation success message should be displayed");
////    }
////
////    @Test
////    @Order(5)
////    public void testCommentaires() {
////        driver.get(BASE_URL + "/comment/create");
////
////        ObjectNode commentData = objectMapper.createObjectNode()
////                .put("text", "Test Comment");
////
////        WebElement commentInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("comment-text")));
////        commentInput.sendKeys(commentData.get("text").asText());
////
////        WebElement commentSubmit = driver.findElement(By.id("comment-submit"));
////        commentSubmit.click();
////
////        WebElement commentSuccess = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("comment-success")));
////        assertTrue(commentSuccess.isDisplayed(), "Comment creation success message should be displayed");
////
////        driver.get(BASE_URL + "/comment/all");
////        WebElement commentsList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("comments-list")));
////        assertTrue(commentsList.isDisplayed(), "Comments list should be visible");
////    }
//}
