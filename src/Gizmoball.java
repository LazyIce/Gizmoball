import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.windows.MainWindow;

public class Gizmoball
{
    public static void main (String[] args)
    {
            try {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Gizmoball");
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) {
                    System.out.println("ClassNotFoundException: " + e.getMessage());
            } catch (InstantiationException e) {
                    System.out.println("InstantiationException: " + e.getMessage());
            } catch (IllegalAccessException e) {
                    System.out.println("IllegalAccessException: " + e.getMessage());
            } catch(UnsupportedLookAndFeelException e) {
                    System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
            }
            
            MainWindow window = new MainWindow("Gizmoball");
            // center the window
            window.setLocationRelativeTo(null);
    		
            window.setVisible(true);
            window.run();
    }
}