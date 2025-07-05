// File: ValidationUtils.java
package validator;

public class ValidationUtils {

	public static boolean isValidTIN(String tin) {
		return tin != null && tin.matches("[A-Z0-9]{12,14}");

	}

	public static boolean isValidBRN(String brn) {
		return brn != null && brn.matches("\\d{12,20}");
	}

	public static boolean isValidEmail(String email) {
		return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
	}

	public static boolean isValidPhone(String phone) {
		return phone != null && phone.matches("\\d{10,15}");
	}

	public static boolean validateRegistrationOrID(String schemeID, String value) {
		if (value == null || value.trim().isEmpty() || schemeID == null)
			return false;
		value = value.trim();
		switch (schemeID.toUpperCase()) {
		case "NRIC":
		case "PASSPORT":
		case "ARMY":
			return value.matches("[A-Za-z0-9]+") && value.length() <= 12;
		case "BRN":
			return value.matches("[A-Za-z0-9]+") && value.length() <= 20;
		default:
			return false;
		}
	}

	public static boolean validateSST(String value) {
		if (value == null || value.trim().isEmpty())
			return false;
		value = value.trim();
		if (value.equalsIgnoreCase("NA"))
			return true;
		if (value.length() > 35)
			return false;
		return value.matches("([A-Za-z0-9-]+)(;[A-Za-z0-9-]+)?");
	}

	public static boolean validateTTX(String value) {
		if (value == null || value.trim().isEmpty())
			return false;
		value = value.trim();
		if (value.equalsIgnoreCase("NA"))
			return true;
		if (value.length() > 17)
			return false;
		return value.matches("[A-Za-z0-9-]+");
	}
}