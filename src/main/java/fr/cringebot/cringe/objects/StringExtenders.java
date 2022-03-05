/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   StringExtenders.java                               :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: gchatain <gchatain@student.42lyon.fr>      +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2021/12/05 13:22:04 by gchatain          #+#    #+#             */
/*   Updated: 2022/03/05 18:13:58 by                  ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

package fr.cringebot.cringe.objects;


public class StringExtenders {
    /**
     * sert à voir si String contient search en ignorant les majs
     *
     * @param string string de base
     * @param search string a chercher
     * @return si il contient
     */
    public static boolean containsIgnoreCase(String string, String search) {
        return BetterLowCase(string).contains(BetterLowCase(search));
    }

    /**
     * sert a voir si String commence par String2 en ignorant les majs
     *
     * @param string
     * @param string2
     * @return
     */
    public static boolean startWithIgnoreCase(String string, String string2) {
        return string.toLowerCase().startsWith(string2.toLowerCase());
    }

    /**
     * donner l'index de l'emplacement de où il a trouver le string2 dans string
     *
     * @param string le string de base
     * @param search le string a chercher dans la base
     * @return
     */
    public static int firstsearch(String string, String search) {
        int i = 0;
        int j = 0;
        while (string.charAt(i) != 0) {
            if (string.charAt(i) == search.charAt(j))
                j++;
            else
                j = 0;
            if (search.length() <= j)
                return i;
            i++;
        }
        return i;
    }

    /**
    condition modifié de uppercase pour la fonction rage
    */
    public static boolean isUpperCase(String s){
        int i = 0;
        int c = 0;
        while (s.length() > i){
            if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z'){
                c++;
            }
            else if (c >= 5)
                return true;
            else
                c = 0;
            i++;
        }
        return c >= 5;
    }

    private static String BetterLowCase(String str) {
        return (str.replace('ï', 'i').replace('ô', 'o').replace('é', 'e').replace('è', 'e').replace('ç', 'c').replace('É', 'e'));
    }

    public static boolean BetterIgnoreCase(String str1, String str2){
        str1 = BetterLowCase(str1);
        str2 = BetterLowCase(str2);
        return (str1.equalsIgnoreCase(str2));
    }
}


