public class IPValidator {
    public static boolean validateIPv4Address(String str){

        String[] numbers = str.split("\\.");
        if(numbers.length != 4)
            return false;
        for (int i = 0; i < numbers.length;i++){
            if(numbers[i].isEmpty())
                return false;

            if(numbers[i].charAt(0) == '0' && numbers[i].length() > 1 )
                return false;

            if(!numbers[i].chars().allMatch( Character :: isDigit))
                return false;

            int number = Integer.parseInt(numbers[i]);
            if( number < 0 || number > 255)
                return false;
        }
        return true;
    }

}
