namespace MonsterPet
{
    partial class FrmSelPagamento
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FrmSelPagamento));
            this.btCredito = new System.Windows.Forms.Button();
            this.btDebito = new System.Windows.Forms.Button();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.btAvancar = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // btCredito
            // 
            this.btCredito.FlatAppearance.BorderSize = 0;
            this.btCredito.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btCredito.Image = ((System.Drawing.Image)(resources.GetObject("btCredito.Image")));
            this.btCredito.Location = new System.Drawing.Point(76, 95);
            this.btCredito.Name = "btCredito";
            this.btCredito.Size = new System.Drawing.Size(305, 170);
            this.btCredito.TabIndex = 0;
            this.btCredito.UseVisualStyleBackColor = true;
            this.btCredito.Click += new System.EventHandler(this.btCredito_Click);
            // 
            // btDebito
            // 
            this.btDebito.FlatAppearance.BorderSize = 0;
            this.btDebito.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btDebito.Image = ((System.Drawing.Image)(resources.GetObject("btDebito.Image")));
            this.btDebito.Location = new System.Drawing.Point(438, 95);
            this.btDebito.Name = "btDebito";
            this.btDebito.Size = new System.Drawing.Size(305, 170);
            this.btDebito.TabIndex = 1;
            this.btDebito.UseVisualStyleBackColor = true;
            this.btDebito.Click += new System.EventHandler(this.btDebito_Click);
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
            this.pictureBox1.Location = new System.Drawing.Point(270, 21);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(284, 22);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.AutoSize;
            this.pictureBox1.TabIndex = 2;
            this.pictureBox1.TabStop = false;
            // 
            // btAvancar
            // 
            this.btAvancar.FlatAppearance.BorderSize = 0;
            this.btAvancar.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btAvancar.Image = ((System.Drawing.Image)(resources.GetObject("btAvancar.Image")));
            this.btAvancar.Location = new System.Drawing.Point(304, 315);
            this.btAvancar.Name = "btAvancar";
            this.btAvancar.Size = new System.Drawing.Size(207, 48);
            this.btAvancar.TabIndex = 3;
            this.btAvancar.UseVisualStyleBackColor = true;
            this.btAvancar.Click += new System.EventHandler(this.btAvancar_Click);
            // 
            // FrmSelPagamento
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.White;
            this.ClientSize = new System.Drawing.Size(815, 413);
            this.Controls.Add(this.btAvancar);
            this.Controls.Add(this.pictureBox1);
            this.Controls.Add(this.btDebito);
            this.Controls.Add(this.btCredito);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Name = "FrmSelPagamento";
            this.StartPosition = System.Windows.Forms.FormStartPosition.Manual;
            this.Text = "FrmSelPagamento";
            this.Shown += new System.EventHandler(this.FrmSelPagamento_Shown);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btCredito;
        private System.Windows.Forms.Button btDebito;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.Button btAvancar;
    }
}