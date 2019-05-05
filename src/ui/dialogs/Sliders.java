package ui.dialogs;


import java.awt.*;


public class Sliders extends javax.swing.JDialog {

	private javax.swing.JButton btnAccept;
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblHeight;
    private javax.swing.JLabel lblWidth;
    private javax.swing.JSlider sliderH;
    private javax.swing.JSlider sliderW;
    
    private Point m_Value;

    
    public Sliders(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        m_Value = new Point();
        initComponents();
        m_Value.x = sliderW.getValue()+1;
        lblWidth.setText("Width: " + m_Value.x);
        m_Value.y = sliderH.getValue()+1;
        lblHeight.setText("Height: " + m_Value.y);
    }

    
    private void initComponents() {

        sliderW = new javax.swing.JSlider();
        sliderH = new javax.swing.JSlider();
        lblHeight = new javax.swing.JLabel();
        lblWidth = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnAccept = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        sliderW.setMaximum(19);
        sliderW.setMinorTickSpacing(1);
        sliderW.setPaintTicks(true);
        sliderW.setValue(0);
        sliderW.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderWStateChanged(evt);
            }
        });

        sliderH.setMaximum(19);
        sliderH.setMinorTickSpacing(1);
        sliderH.setPaintTicks(true);
        sliderH.setValue(0);
        sliderH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderHStateChanged(evt);
            }
        });

        lblHeight.setText("Height:");

        lblWidth.setText("Width:");

        jLabel3.setText("Enter a height and a width:");

        btnAccept.setText("Accept");
        btnAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(lblHeight)
                    .addComponent(sliderH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(sliderW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblWidth, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAccept)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                        .addComponent(btnCancel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHeight)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblWidth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAccept)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }



    private void sliderWStateChanged(javax.swing.event.ChangeEvent evt) {
        m_Value.x = sliderW.getValue()+1;
        lblWidth.setText("Width: " + m_Value.x);
    }

    private void sliderHStateChanged(javax.swing.event.ChangeEvent evt) {
        m_Value.y = sliderH.getValue()+1;
        lblHeight.setText("Height: " + m_Value.y);
    }

    private void btnAcceptActionPerformed(java.awt.event.ActionEvent evt) {
        //Dispose of this, unblocking the program and allowing us to
        //get our value.
        this.dispose();
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        //set the value to null. We don't want a meaningful return
        //If the user cancelled.
        m_Value = null;
        this.dispose();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        m_Value = null;
    }

    public Point getValue() {return m_Value;}

}