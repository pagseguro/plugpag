namespace MonsterPet
{
    partial class FrmLogin
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FrmLogin));
            this.panel1 = new System.Windows.Forms.Panel();
            this.tbxSenha = new System.Windows.Forms.TextBox();
            this.tbxNome = new System.Windows.Forms.TextBox();
            this.btLogin = new System.Windows.Forms.Button();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            // 
            // panel1
            // 
            this.panel1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("panel1.BackgroundImage")));
            this.panel1.Controls.Add(this.tbxSenha);
            this.panel1.Controls.Add(this.tbxNome);
            this.panel1.Controls.Add(this.btLogin);
            this.panel1.Location = new System.Drawing.Point(12, 12);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(207, 100);
            this.panel1.TabIndex = 7;
            // 
            // tbxSenha
            // 
            this.tbxSenha.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.tbxSenha.CharacterCasing = System.Windows.Forms.CharacterCasing.Upper;
            this.tbxSenha.Location = new System.Drawing.Point(3, 22);
            this.tbxSenha.MaxLength = 10;
            this.tbxSenha.Name = "tbxSenha";
            this.tbxSenha.PasswordChar = '*';
            this.tbxSenha.Size = new System.Drawing.Size(107, 13);
            this.tbxSenha.TabIndex = 8;
            this.tbxSenha.Enter += new System.EventHandler(this.tbxSenha_Enter);
            this.tbxSenha.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbxSenha_KeyDown);
            // 
            // tbxNome
            // 
            this.tbxNome.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.tbxNome.CharacterCasing = System.Windows.Forms.CharacterCasing.Upper;
            this.tbxNome.Location = new System.Drawing.Point(3, 3);
            this.tbxNome.MaxLength = 20;
            this.tbxNome.Name = "tbxNome";
            this.tbxNome.Size = new System.Drawing.Size(107, 13);
            this.tbxNome.TabIndex = 7;
            this.tbxNome.Enter += new System.EventHandler(this.tbxNome_Enter);
            this.tbxNome.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbxNome_KeyDown);
            // 
            // btLogin
            // 
            this.btLogin.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("btLogin.BackgroundImage")));
            this.btLogin.FlatAppearance.BorderSize = 0;
            this.btLogin.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btLogin.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btLogin.ForeColor = System.Drawing.Color.Transparent;
            this.btLogin.Location = new System.Drawing.Point(3, 41);
            this.btLogin.Name = "btLogin";
            this.btLogin.Size = new System.Drawing.Size(147, 38);
            this.btLogin.TabIndex = 9;
            this.btLogin.UseVisualStyleBackColor = true;
            this.btLogin.Click += new System.EventHandler(this.btLogin_Click);
            // 
            // FrmLogin
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.Gray;
            this.ClientSize = new System.Drawing.Size(231, 124);
            this.Controls.Add(this.panel1);
            this.DoubleBuffered = true;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.KeyPreview = true;
            this.Name = "FrmLogin";
            this.Text = "PagSeguro MonsterPet";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FrmLogin_FormClosing);
            this.KeyDown += new System.Windows.Forms.KeyEventHandler(this.FrmLogin_KeyDown);
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.TextBox tbxSenha;
        public System.Windows.Forms.TextBox tbxNome;
        private System.Windows.Forms.Button btLogin;
    }
}

