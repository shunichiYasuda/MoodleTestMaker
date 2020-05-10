package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	final String headText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<quiz>\r\n" + 
			"<!-- question: 0  -->\r\n" + 
			"  <question type=\"category\">\r\n" + 
			"    <category>\r\n" + 
			"        <text>$course$/test2020 �f�t�H���g</text>\r\n" + 
			"\r\n" + 
			"    </category>\r\n" + 
			"  </question>\r\n" + 
			"\r\n" + 
			"<!-- question: 27868  -->\r\n" + 
			"  <question type=\"cloze\">\r\n" + 
			"    <name>\r\n" + 
			"      <text>��薼�͂���</text>\r\n" + 
			"    </name>\r\n" + 
			"    <questiontext format=\"html\">\r\n" + 
			"      <text><![CDATA[";
	final String tailText =" ]]></text>\r\n" + 
			"    </questiontext>\r\n" + 
			"    <generalfeedback format=\"html\">\r\n" + 
			"      <text></text>\r\n" + 
			"    </generalfeedback>\r\n" + 
			"    <penalty>0.3333333</penalty>\r\n" + 
			"    <hidden>0</hidden>\r\n" + 
			"  </question>\r\n" + 
			"\r\n" + 
			"</quiz>";
	@FXML
	TextArea mainArea;
	@FXML
	TextArea outputArea;
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
		if (!mainArea.getText().trim().isEmpty()) {
			mainArea.clear();
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
		//���e�L�X�g
		String str = mainArea.getText();
		//�����ς݃e�L�X�g
		String strBody = "";
		// ���̃e�L�X�g��I����<s:....>���Ƃɕ�����
		String[] array = str.split("<s:.+?>");
		// �I�����̐ݒ�
		String regex = "<s:.+?>";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		List<String> itemList = new ArrayList<String>();
		while (m.find()) {
			String s = m.group();
			System.out.println(s);
			s = s.replace("<s:", "");
			s = s.replace(">", "");
			itemList.add(s);
		}
		// array �̗v�f���Ƃɏ���
		for (int i = 0; i < array.length; i++) {
			//System.out.println("+------loop " + i + "  ---------");
			String target = array[i];
			String regexQ = "<q:.+?>";
			Pattern q = Pattern.compile(regexQ);
			Matcher mq = q.matcher(target);
			while (mq.find()) {
				String oldStr = mq.group();
				//System.out.println(oldStr);
				// ���𕶎���̃X�g���b�v
				String correct = oldStr.replace("<q:", "");
				correct = correct.replace(">", "");
				// �V�K�̃X�g�����O���쐬
				String headStr = "{1:MULTICHOICE:";
				String[] itemArray = itemList.get(i).split(",");
				for (int j = 0; j < itemArray.length; j++) {
					if (itemArray[j].equals(correct)) {
						itemArray[j] = "=" + itemArray[j];
					}
				}
				String subStr = "";
				for (String s : itemArray) {
					s = "~" + s;
					subStr += s;
				}
				subStr = subStr.replaceFirst("~", "");
				//System.out.println(subStr);
				String newStr = headStr + subStr + "}";
				//System.out.println(newStr);
				target = target.replace(oldStr, newStr);
			} // end of while(mq.find()}
			//System.out.println(target);
			strBody += target;
		} // end of for(int i=0; ...
		outputArea.setText(strBody);
		System.out.println(strBody);
		
	} // end of translate()
}
