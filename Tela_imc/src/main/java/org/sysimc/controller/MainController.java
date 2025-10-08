package org.sysimc.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.sysimc.model.Pessoa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;

public class MainController {

    @FXML
    public TextField txtNome;

    @FXML
    public TextField txtAltura;

    @FXML
    public TextField txtPeso;

    @FXML
    public Label lblIMC;

    @FXML
    public Label lblImc;

    @FXML
    public Label lblClassificacao;

    Pessoa pessoa = new Pessoa();
    private static final String Banco_de_Dados = "dados_pessoas.txt";

  /*  @FXML
    protected void onCalcularIMCClick() {
        DecimalFormat df = new DecimalFormat();
        this.pessoa.setNome(txtNome.getText() );
        this.pessoa.setAltura(Float.parseFloat(txtAltura.getText()) );
        this.pessoa.setPeso(Float.parseFloat(txtPeso.getText() ));

        df.applyPattern("#0.00");
        this.lblIMC.setText(df.format( this.pessoa.calcularIMC()) );
        this.lblClassificacao.setText( this.pessoa.classificacaoIMC() );
    }
    */

    @FXML
    protected void onCalcularIMCClick() {
        // Validação básica para evitar erros de conversão se o campo estiver vazio
        if (txtAltura.getText().isEmpty() || txtPeso.getText().isEmpty()) {
            this.lblIMC.setText("---");
            this.lblClassificacao.setText("Preencha Altura e Peso");
            return;
        }

        try {
            DecimalFormat df = new DecimalFormat();

            // 1. Atualiza os dados da pessoa
            this.pessoa.setNome(txtNome.getText() );
            // Converte o texto para float, usando a localização padrão ou ponto ('.')
            this.pessoa.setAltura(Float.parseFloat(txtAltura.getText().replace(',', '.')) );
            this.pessoa.setPeso(Float.parseFloat(txtPeso.getText().replace(',', '.')) );

            // 2. Calcula IMC e atualiza os rótulos
            df.applyPattern("#0.00");
            this.lblIMC.setText(df.format( this.pessoa.calcularIMC()) );
            this.lblClassificacao.setText( this.pessoa.classificacaoIMC() );

        } catch (NumberFormatException e) {
            this.lblIMC.setText("ERRO");
            this.lblClassificacao.setText("Altura/Peso inválidos");
            System.err.println("Erro de formato de número: " + e.getMessage());
        }
    }

    /*@FXML
    protected void onSalvarClick() {
        // Aqui você decide onde salvar (por exemplo, arquivo ou lista)
        System.out.println("Salvando pessoa...");
        System.out.println("Nome: " + pessoa.getNome());
        System.out.println("Altura: " + pessoa.getAltura());
        System.out.println("Peso: " + pessoa.getPeso());
        System.out.println("IMC: " + pessoa.getImc());


    }*/

    @FXML
    protected void onSalvarClick() {
        // Verifica se o IMC foi calculado antes de salvar
        if (this.pessoa.getImc() == 0.0f) {
            System.out.println("IMC não calculado. Calcule antes de salvar.");
            // Opcional: Mostrar alerta na interface para o usuário
            return;
        }

        // Define o nome do arquivo onde os dados serão salvos
        String nomeArquivo = "dados_pessoas.txt";

        try (FileWriter arq = new FileWriter(Banco_de_Dados, true); // 'true' para adicionar ao final (append)
             PrintWriter gravarArq = new PrintWriter(arq)) {

            // Formato de linha: Nome;Altura(cm);Peso(kg);IMC;Classificacao
            String linha = String.format("%s;%.2f;%.2f;%.2f;%s",
                    this.pessoa.getNome(),
                    this.pessoa.getAltura(),
                    this.pessoa.getPeso(),
                    this.pessoa.getImc(),
                    this.pessoa.classificacaoIMC()
            );

            gravarArq.println(linha);

            System.out.println("Dados salvos com sucesso em: " + nomeArquivo);

            txtNome.setText("");
            txtAltura.setText("");
            txtPeso.setText("");

            lblIMC.setText("00.00"); // Ou "---"
            lblClassificacao.setText("");


            this.pessoa = new Pessoa();

            System.out.println("Linha salva: " + linha);

        } catch (IOException e) {
            System.err.println("Erro ao salvar os dados no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

   /* @FXML
    protected void onCarregarClick() {
        // Simula dados carregados de algum lugar (ex: banco, arquivo, API, etc.)
        String nomeExemplo = "Eduardo";
        double alturaExemplo = 170;
        double pesoExemplo = 80;

        // Preenche os campos da tela
        txtNome.setText(nomeExemplo);
        txtAltura.setText(String.valueOf(alturaExemplo));
        txtPeso.setText(String.valueOf(pesoExemplo));

        // Atualiza os cálculos automaticamente
        double imc = pesoExemplo / Math.pow(alturaExemplo / 100, 2);
        lblImc.setText(String.format("%.2f", imc));

        // Classificação automática
        String classificacao;
        if (imc < 18.5) classificacao = "Abaixo do peso";
        else if (imc < 25) classificacao = "Peso normal";
        else if (imc < 30) classificacao = "Sobrepeso";
        else classificacao = "Obesidade";

        lblClassificacao.setText(classificacao);

        System.out.println("Dados carregados: " + nomeExemplo);
    }
    */

    @FXML
    protected void onCarregarClick() {
        File arquivo = new File(Banco_de_Dados);

        // 1. Verifica se o arquivo existe e se não está vazio
        if (!arquivo.exists() || arquivo.length() == 0) {
            this.lblIMC.setText("---");
            this.lblClassificacao.setText("Nenhum dado salvo para carregar.");
            System.out.println("O arquivo de dados não existe ou está vazio.");
            return;
        }

        try {
            // 2. Lê todas as linhas do arquivo
            List<String> linhas = Files.readAllLines(Paths.get(Banco_de_Dados));

            if (linhas.isEmpty()) {
                this.lblIMC.setText("---");
                this.lblClassificacao.setText("Nenhum dado salvo para carregar.");
                return;
            }

            // 3. Pega a última linha salva (o registro mais recente)
            String ultimaLinha = linhas.get(linhas.size() - 1);

            // 4. Divide a linha usando o ponto e vírgula (;) como separador
            // Formato: Nome;Altura(cm);Peso(kg);IMC;Classificacao
            String[] dados = ultimaLinha.split(";");

            // 5. Valida se a linha tem pelo menos os 3 dados essenciais (Nome, Altura, Peso)
            if (dados.length >= 3) {
                String nome = dados[0];
                String alturaStr = dados[1];
                String pesoStr = dados[2];

                // Preenche os campos da tela
                txtNome.setText(nome);
                txtAltura.setText(alturaStr);
                txtPeso.setText(pesoStr);

                // 6. Chama a lógica de cálculo para atualizar IMC e Classificação automaticamente
                onCalcularIMCClick();

                System.out.println("Dados carregados com sucesso (último registro): " + nome);

            } else {
                this.lblIMC.setText("ERRO");
                this.lblClassificacao.setText("Formato de dados inválido.");
                System.err.println("Erro: O formato da última linha do arquivo está incorreto.");
            }

        } catch (IOException e) {
            this.lblIMC.setText("ERRO");
            this.lblClassificacao.setText("Erro ao ler o arquivo.");
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }


}