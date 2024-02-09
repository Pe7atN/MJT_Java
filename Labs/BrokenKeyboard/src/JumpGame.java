public class JumpGame {

    public static boolean canWinIndex(int[] array, int index){

        if(index == array.length - 1)
            return true;

        if(index >= array.length)
            return false;

        if(array[index] == 0)
            return false;

        for (int i = array[index]; i >=1; i--)
            if(canWinIndex(array, index + i))
                return true;

        return false;
    }

    public static boolean canWin(int[] array){
        return canWinIndex(array,0);

    }

}
