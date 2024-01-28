import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class NotepadGUI extends JFrame {
    // file explorer
    private JFileChooser fileChooser;

    private JTextArea textArea;

    public JTextArea getTextArea() {
        return textArea;
    }

    private File currentFile;

    // swing's built in library to manage undo and redo functionalities
    private UndoManager undoManager;

    public NotepadGUI() {
        super("Notepad");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //file chooser setup
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/assets"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files","txt"));

        undoManager = new UndoManager();
        addGuiComponents();

    }

    private void addGuiComponents(){
        addToolbar();

        //area to type text
        textArea = new JTextArea();
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                //add each edit that we do in text area
                undoManager.addEdit(e.getEdit());
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        //menu bar
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // add menus
        menuBar.add(addFileMenu());
        menuBar.add(addEditMenu());
        menuBar.add(addFormatMenu());
        menuBar.add(addViewMenu());

        add(toolBar, BorderLayout.NORTH);
    }

    private JMenu addFileMenu() {
        JMenu fileMenu = new JMenu("File");

        //new Functionality reset everything
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //reset title header
                setTitle("Notepad");

                //reset Text area
                textArea.setText("");

                //reset current file
                currentFile = null;
            }
        });
        fileMenu.add(newMenuItem);
        //open functionality open text file
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //open file explorer
                int result = fileChooser.showOpenDialog(NotepadGUI.this);

                if (result != JFileChooser.APPROVE_OPTION) return;
                try {
                    //reset  notepad
                    newMenuItem.doClick();

                    //get the selected file
                    File selectedFile = fileChooser.getSelectedFile();

                    //update current file
                    currentFile = selectedFile;

                    //update title header
                    setTitle(selectedFile.getName());

                    //read the file
                    FileReader fileReader = new FileReader(selectedFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    //store the text
                    StringBuilder fileText = new StringBuilder();
                    String readText;
                    while ((readText = bufferedReader.readLine()) != null) {
                        fileText.append(readText + "\n");
                    }

                    //update text area gui
                    textArea.setText(fileText.toString());

                } catch (Exception e1) {

                }
            }

        });
        fileMenu.add(openMenuItem);

        //save as functionality
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //open save dialog
                int result = fileChooser.showSaveDialog(NotepadGUI.this);

                //continue to execute only if user presssed save button
                if(result != JFileChooser.APPROVE_OPTION) return;
                try{
                    File selectedFile = fileChooser.getSelectedFile();

                    //need to append .txt to file if it does not have ,txt extension
                    String fileName = selectedFile.getName();
                    if(!fileName.substring(fileName.length()-4).equalsIgnoreCase(".txt")){
                        selectedFile = new File(selectedFile.getAbsoluteFile()+".txt";
                    }

                    //create new file
                    selectedFile.createNewFile();

                    //write users text into file that was created
                    FileWriter fileWriter = new FileWriter(selectedFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();

                    //update the title header of gui to the save text file
                    setTitle(fileName);

                    //update current file
                    currentFile = selectedFile;

                    //show display dialog
                    JOptionPane.showMessageDialog(NotepadGUI.this,"Saved File!");
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });
         fileMenu.add(saveAsMenuItem);
         // save functionality save text into current file
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if current file is null then perform save as
                if(currentFile == null) saveMenuItem.doClick();

                //if user chooses to cancel saving the file this means current fill will be null
                //prevent executing the rest of code
                if(currentFile == null)return;

                try{
                    //write to current file
                    FileWriter fileWriter = new FileWriter(currentFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        fileMenu.add(saveMenuItem);

        //exit functionality ends program
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //dispose gui
                NotepadGUI.this.dispose();
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;

    }
    private JMenu addEditMenu(){
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if there are edits to undo, undo them
                if(undoManager.canUndo()){
                    undoManager.undo();
                }
            }
        });
    }
}