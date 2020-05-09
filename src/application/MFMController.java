package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.universalchardet.UniversalDetector;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class MFMController {
	String encode = null;
	String sysEncode = null;
	@FXML
	TextArea mainArea;
	@FXML
	TextArea dummyArea;

	@FXML
	private void quitAction() {
		System.exit(0);
	}

	@FXML
	private void openAction() {
		// �V�X�e�������R�[�h
		sysEncode = System.getProperty("file.encoding");
		FileChooser fc = new FileChooser();
		fc.setTitle("Open data file");
		fc.setInitialDirectory(new File("."));
		File file = fc.showOpenDialog(null);
		if (file == null) {
			return;
		}
		// �����R�[�h����
		try {
			encode = detectEncoding(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (encode == null) {
			encode = sysEncode;
		}
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), encode);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				mainArea.appendText(line + "\n");
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �����R�[�h�`�F�b�N
	private String detectEncoding(File file) throws IOException {
		String result = null;
		byte[] buf = new byte[4096];
		FileInputStream fis = new FileInputStream(file);
		UniversalDetector detector = new UniversalDetector(null);
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		detector.dataEnd();
		result = detector.getDetectedCharset();
		detector.reset();
		fis.close();
		return result;

	}

	//
	@FXML
	private void test() {
		System.out.println(System.getProperty("file.encoding"));
	}

	//
	@FXML
	private void clearAction() {
		if (mainArea.getText() != null) {
			mainArea.clear();
		}
		if (dummyArea.getText() != null) {
			dummyArea.clear();
		}
		return;
	}

	@FXML
	private void saveAction() {
		// �V�X�e�������R�[�h
		sysEncode = System.getProperty("file.encoding");
		FileChooser fc = new FileChooser();
		fc.setTitle("Set save file");
		fc.setInitialDirectory(new File("."));
		File file = fc.showSaveDialog(null);
		if (file == null) {
			return;
		}
		if (encode.equals(null)) {
			encode = sysEncode;
		}
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), encode);
			BufferedWriter bw = new BufferedWriter(osw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(mainArea.getText());
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void translate() {
		//"{\toi:***}" �̃��X�g
		//List<String> toiList = new ArrayList<String>();
		// �I�����̃��X�g
		List<String> itemList = new ArrayList<String>();
		// mainArea �̕����񂩂�{\toi}��T���B
		String str = mainArea.getText();
		//�ȉ��̐��K�\����{\toi�F*}��T���Ă���B
		//"\\{" �� '{' �A\\ ��'\' �̃G�X�P�[�v�B����"\\\t" �͓��ꕶ��'\'���G�X�P�[�v�������
		//"\t" ���G�X�P�[�v���邽�߂ɂ������'\'���K�v�B
		String regex = "\\{\\\\toi:.+?}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()) {
			String s = m.group().replace("{\\toi:", "");
			s=s.replace("}", "");
			itemList.add(s);
		}
		//dummy �̒ǉ�
		if(dummyArea.getText()!=null) {
			String[] array = dummyArea.getText().split(",");
			for(String s:array) {
				itemList.add(s);
			}
		}
		for(String s: itemList) {
			System.out.println(s);
		}
	}
}
