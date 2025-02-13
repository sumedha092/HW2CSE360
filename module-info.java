module TeamHW1 {
	requires java.base;
	requires javafx.controls;
	requires java.sql;
	requires org.junit.jupiter.api;
	
	opens application to javafx.graphics, javafx.fxml;
}
