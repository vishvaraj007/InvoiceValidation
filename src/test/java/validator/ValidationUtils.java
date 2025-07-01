package validator;

public class ValidationUtils {

	// ✅ Validate TIN: 12 characters, uppercase letters or digits
	public static boolean isValidTIN(String tin) {
		return tin != null && tin.matches("[A-Z0-9]{12,14}");
	}

	// ✅ Validate BRN: Exactly 10 digits
	public static boolean isValidBRN(String brn) {
		return brn != null && brn.matches("\\d{10,20}");
	}

	// ✅ Validate Email (simple pattern)
	public static boolean isValidEmail(String email) {
		return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
	}

	// ✅ Validate Phone Number (10 to 15 digits)
	public static boolean isValidPhone(String phone) {
		return phone != null && phone.matches("\\d{10,15}");
	}

	// ✅ Shared for both Supplier & Buyer Registration / ID / Passport Number
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

	// ✅ Shared for both Supplier & Buyer SST Registration Number
	public static boolean validateSST(String value) {
		if (value == null || value.trim().isEmpty())
			return false;
		value = value.trim();

		if (value.equalsIgnoreCase("NA"))
			return true;

		if (value.length() > 35)
			return false;

		// Allow 1 or 2 values separated by semicolon, only dash allowed
		return value.matches("([A-Za-z0-9-]+)(;[A-Za-z0-9-]+)?");
	}

	// ✅ Shared for both Supplier & Buyer Tourism Tax Registration Number
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

	// Optional: Main for testing
	public static void main(String[] args) {
		System.out.println("TIN Valid? " + isValidTIN("ABCD12345678")); // true
		System.out.println("BRN Valid? " + isValidBRN("1234567890")); // true
		System.out.println("Email Valid? " + isValidEmail("test@mail.com")); // true
		System.out.println("Phone Valid? " + isValidPhone("0123456789")); // true
	}
}

/*
 * package validator;
 * 
 * public class ValidationUtils {
 * 
 * // ✅ Validate TIN: 12 characters, uppercase letters or digits public static
 * boolean isValidTIN(String tin) { return tin != null &&
 * tin.matches("[A-Z0-9]{12}"); }
 * 
 * // ✅ Validate BRN: Exactly 10 digits public static boolean isValidBRN(String
 * brn) { return brn != null && brn.matches("\\d{10}"); }
 * 
 * // ✅ Validate Name: Not empty public static boolean isValidName(String name)
 * { return name != null && !name.trim().isEmpty(); }
 * 
 * // ✅ Validate Email (simple pattern) public static boolean
 * isValidEmail(String email) { return email != null &&
 * email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"); }
 * 
 * // ✅ Validate Phone Number (at least 10 digits) public static boolean
 * isValidPhone(String phone) { return phone != null &&
 * phone.matches("\\d{10,15}"); }
 * 
 * // ✅ Optional for testing validations public static void main(String[] args)
 * { System.out.println("TIN Valid? " + isValidTIN("ABCD12345678")); // ✅ true
 * System.out.println("BRN Valid? " + isValidBRN("1234567890")); // ✅ true
 * System.out.println("Email Valid? " + isValidEmail("test@mail.com")); // ✅
 * true System.out.println("Phone Valid? " + isValidPhone("0123456789")); // ✅
 * true } }
 */