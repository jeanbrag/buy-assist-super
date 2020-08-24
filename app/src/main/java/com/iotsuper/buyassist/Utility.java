package com.iotsuper.buyassist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

public class Utility {
    public static boolean statusInternet_MoWi(Context context) {

        boolean status = false;
        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conexao != null){
            // PARA DISPOSTIVOS NOVOS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities recursosRede = conexao.getNetworkCapabilities(conexao.getActiveNetwork());
                if (recursosRede != null) {//VERIFICAMOS SE RECUPERAMOS ALGO
                    if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        //VERIFICAMOS SE DISPOSITIVO TEM 3G
                        return true;
                    }
                    else if (recursosRede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        //VERIFICAMOS SE DISPOSITIVO TEM WIFFI
                        return true;
                    }

                    //NÃO POSSUI UMA CONEXAO DE REDE VÁLIDA
                    return false;
                }

            } else {
                // PARA DISPOSTIVOS ANTIGOS  (PRECAUÇÃO)         MESMO CODIGO
                NetworkInfo informacao = conexao.getActiveNetworkInfo();

                if (informacao != null && informacao.isConnected()) {
                    status = true;
                } else
                    status = false;

                return status;
            }
        }
        return false;
    }

    public static void opcoesErro(Context context, String resposta){
        if(resposta.contains("least 6 characters")){
            Toast.makeText(context,"Digite uma senha maior que 5 caracteres", Toast.LENGTH_LONG).show();
        }
        else if(resposta.contains("address is badly")){
            Toast.makeText(context,"Email Inválido", Toast.LENGTH_LONG).show();
        }

        else if(resposta.contains("address is already")){
            Toast.makeText(context,"Email já cadastrado", Toast.LENGTH_LONG).show();
        }
        else if(resposta.contains("interrupted connection")){
            Toast.makeText(context,"Sem conexão com o Firebase", Toast.LENGTH_LONG).show();
        }

        else if(resposta.contains("password is invalid")){
            Toast.makeText(context,"Email ou Senha inválidos", Toast.LENGTH_LONG).show();
        }

        else if(resposta.contains("no user record")){
            Toast.makeText(context,"Usuário não cadastrado", Toast.LENGTH_LONG).show();
        }

        else Toast.makeText(context,resposta, Toast.LENGTH_LONG).show();
    }
}
