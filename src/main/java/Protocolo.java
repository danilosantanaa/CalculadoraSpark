import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocolo {
    private int id_action;
    private String dados;
    private String response;
    private int status;
    private String operatorName;
    private String result;

    public Protocolo(String dados) {
        this.setDados(dados);
    }

    public Protocolo(byte[] dados) {
        this.setDados(dados);
    }

    public String getDados() {
        return this.dados;
    }

    public Protocolo processarDados() {
        /*
        * Expressão regular para validar entrada de dados enviado pelo cliente e conforme com a validação,
        * será redirecionado para a operação, caso os dados enviado pelo cliente esteja correto.
        * Verifica quais operações será realizado e jogar o ID da operação.
        * */
        if(Pattern.matches("SOMA[0-9\\s\\n\\+?]+", this.getDados())) this.id_action = 1;
        else if(Pattern.matches("MULT[0-9\\s\\n\\*]+", this.getDados())) this.id_action = 2;
        else if(Pattern.matches("SUB[0-9\\s\\n\\-]+", this.getDados())) this.id_action = 3;
        else if(Pattern.matches("DIV[0-9\\s\\n\\/]+", this.getDados())) this.id_action = 4;
        else if(Pattern.matches("(PORC|porc)\\s*[0-9]+\\s*\\%\\s*[0-9]+\n?", this.getDados())) this.id_action = 5;
        else if(Pattern.matches("(POT|pot)(\\s*[0-9]+\\s*\\^\\s*[0-9]+)(\n?)", this.getDados())) this.id_action = 6;
        else if(Pattern.matches("(RAIZQ|raizQ)(\\s*[0-9]+)(\n?)", this.getDados())) this.id_action = 7;
        else if (Pattern.matches("DATA\n", this.getDados())) this.id_action = 8;
        else if (Pattern.matches("BYE\n", this.getDados())) this.id_action = 9;
        else if (Pattern.matches("(AJUDA|HELP)\n", this.getDados())) this.id_action = 10;

        // Realizando operação de acordo com a validação da expressão regular
        this.realizarProcesso();
        return this;
    }

    private void realizarProcesso() {
        /*
         * De acordo com a operação solicitada pelo cliente, será redirecionado para a função que realiza a operação
         * */
        this.status = 200;
        switch(this.id_action) {
            case 1: // SOMA
                this.setResponse(this.somar());
                break;
            case 2: // MULTIPLICAÇÃO
                this.setResponse(this.multiplicar());
                break;
            case 3: // SUBTRAIR
                this.setResponse(this.subtrair());
                break;
            case 4: // DIVISÂO
                this.setResponse(this.divisao());
                break;
            case 5: // PORCENTAGEM
                this.setResponse(this.porcentagem());
                break;
            case 6: // POTENCIA
                this.setResponse(this.potencia());
                break;
            case 7: // RAIZ QUADRADA
                this.setResponse(this.raizQuadrada());
                break;
            case 8: // DATA
                this.setResponse(this.getData());
                break;
            case 9: // Encerramento da conexão
                this.status = 100;
                this.setResponse("Cliente sera desconectado. Bye!");
                break;
            case 10: // Manual de uso
                this.setResponse(this.mostrarManual());
                break;
            default:
                this.status = 404; // Not Found "Não encontrado"
        }
    }

    /* Metodo com objetivo de retornar o manual de uso do protocolo */
    public String mostrarManual() {
        String manual = "";
        try {
            File file = new File(new File("").getCanonicalFile() + "\\README.md");
            Scanner sc = new Scanner(file);

            String st;
            while(sc.hasNextLine()) {
                manual += sc.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            manual = "<<< ERRO ao tentar carregar o arquivo. Arquivo nao encontrado!";
            System.err.println(manual);
        } catch (IOException e) {
            manual = e.getMessage();
            e.printStackTrace();
        }

        return manual;
    }

    public int statusCode() {
        return this.status;
    }

    /*
    * Métodos responsáveis por efetuar as 4 operações básicas
    * */
    private String somar(){
        this.result =  operacao('+');
        this.operatorName = "soma";
        return "SOMA = " + this.result;
    }

    private String multiplicar() {
        this.result =  operacao('*');
        this.operatorName = "multiplicar";
        return "MULT = " + operacao('*');
    }

    private String subtrair() {
        this.result =  operacao('-');
        this.operatorName = "subtrair";
        return "SUB = " + operacao('-');
    }

    private String divisao() {
        this.result =  operacao('/');
        this.operatorName = "divisao";
        return "DIV = " + operacao('/');
    }

    /*
    * Métodos responsáveis pelas operações de porcentagem, raiz quadrada e potência
    * */
    public String porcentagem() {
        this.result =  operacao('%');
        this.operatorName = "porcentagem";
        return "PORC = " + operacao('%');
    }

    public String potencia() {
        this.result =  operacao('^');
        this.operatorName = "potencia";
        return "POTENCIA = " + operacao('^');
    }

    public String raizQuadrada() {
        this.result =  operacao('r');
        this.operatorName = "raiz quadrada";
        return "RAIZ = " + operacao('r');
    }

    /*
    * Métodos responsáveis por realizar a operação conforme o operador informado como argumento.
    * */
    private String operacao(char operacao) {
        // Montando expressão regular para pegar os numeros de uma operação
        Pattern patternNum = Pattern.compile("[0-9]+");
        Matcher m = patternNum.matcher(this.getDados().toUpperCase(Locale.ROOT));

        // Realizando a soma
        double result = 0;
        int num_capturado = 0;
        while(m.find()) {
            num_capturado = Integer.parseInt(m.group());
            result = operacao == '+' ? result + num_capturado :
                     operacao == '*' ? (result == 0 ? 1 : result) * num_capturado :
                     operacao == '/' ? (result == 0 ? num_capturado : result / num_capturado) :
                     operacao == '-' ? (result == 0 ? num_capturado : result - num_capturado) :
                     operacao == '%' ? (result == 0 ? num_capturado : result * num_capturado / 100) :
                     operacao == '^' ? (result == 0 ? num_capturado : Math.pow(result, num_capturado)) :
                     operacao == 'r' ?  Math.sqrt(num_capturado) : 0;
        }

        return String.valueOf(result);
    }

    /* Metodo acessor e modificador */
    public void setDados(String dados) {
        this.dados = dados.toUpperCase(Locale.ROOT);
    }

    private void setResponse(String response) {
        this.response = "[SRC: SERVER A] :: ";
        if(this.getID() >= 5 && this.getID() <= 7) {
            this.response = "[SRC: SERVER B] :: ";
        }

        this.response += response;
    }

    public String getResponse() {
        return this.response;
    }

    public void setDados(byte [] dados) {
        // Retirar a parte bínaria da string
        int i = 0;
        for(i = 0; i < dados.length && dados[i] != 0; i++);

        this.setDados((new String(dados, StandardCharsets.UTF_8).trim()));
    }

    // Retorna a data atual do sistema
    private String getData() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime agora = LocalDateTime.now();
        return dtf.format(agora);
    }

    public int getID() {
        return this.id_action;
    }

    public String jsonFormat() {
        return "{\n\t\"" + this.operatorName + "\":" + "\"" + this.result + "\",\n" +
                "\t\"servidor\":" +  "\"servidor-" + (( this.id_action < 5 ? "A" : "B")) + "\"\n" +
                "}";
    }
}
