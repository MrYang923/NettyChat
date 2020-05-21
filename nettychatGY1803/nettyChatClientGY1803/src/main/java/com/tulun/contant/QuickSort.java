package com.tulun.contant;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User:Yang
 * Data:2019/10/25
 * Time:13:12
 */
public class QuickSort {
    public static void quickSort(int[] arr,int low,int high){
        int i,j,tmp,t;
        if (low > high){
            return;
        }
        i = low;
        j = high;

        tmp = arr[low];
        while (i < j){
            while (tmp <= arr[j] && i < j){
                j--;
            }
            while (tmp >= arr[i] && i < j){
                i++;
            }
            if (i < j){
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
            }
        }
        arr[low] = arr[i];
        arr[i] = tmp;

        quickSort(arr,low,j - 1);
        quickSort(arr,j + 1,high);
    }
    public static void main(String[] args) {
        int[] arr = {10,7,2,4,7,62,3,4,2,1,8,9,19};
        quickSort(arr, 0, arr.length-1);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}
