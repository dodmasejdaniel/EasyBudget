package smart.budget.diary.util;

public class CurrencyHelper {
    // get currency in this format:  1200$
    public static String formatCurrency(long money) {
        return money+"$";
    }

    // remove dollar sign from amount: convert 123$ to 123 in our app
    public static long convertAmountStringToLong(CharSequence s) {
//        removes all non-numeric characters from the string and return numbers e.g
//        For example, if s is "abc123def456", after applying replaceAll("[^0-9]", ""), you would get "123456" as the result.
        String cleanString = s.toString().replaceAll("[^0-9]", "");
        if(cleanString.equals("")) return 0;
        return Long.valueOf(cleanString);
    }
}
