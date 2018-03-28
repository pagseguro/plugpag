using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MonsterPet
{
    public partial class FrmLogin : Form
    {
        public FrmLogin()
        {
            InitializeComponent();
            AjustaTela();
        }

        public void AjustaTela()
        {
            this.BackColor = Color.FromArgb(215, 215, 215);

            panel1.Size = new Size(1217, 655);

            panel1.Left = (Screen.PrimaryScreen.Bounds.Width - panel1.Width) / 2;
            panel1.Top = (Screen.PrimaryScreen.Bounds.Height - panel1.Height) / 2;


            tbxNome.Left = 490;
            tbxNome.Top = 360;

            tbxSenha.Left = 490;
            tbxSenha.Top = 430;

            btLogin.Left = 540;
            btLogin.Top = 480;
        }

        private void btLogin_Click(object sender, EventArgs e)
        {
            string nmLogin = "";
            string pwLogin = "";

            nmLogin = tbxNome.Text.Trim();
            pwLogin = tbxSenha.Text;

            if (FrmCompras.userslog.Password == pwLogin)
            {
                FrmCompras.userslog.UserName = nmLogin;
                FrmCompras.userslog.LoginStatus = DialogResult.OK;
                this.DialogResult = DialogResult.OK;
            }
            else
            {
                //senha incorreta
                tbxSenha.Text = "";
                tbxSenha.Focus();
                
            }
        }

        private void FrmLogin_FormClosing(object sender, FormClosingEventArgs e)
        {
            this.DialogResult = DialogResult.OK;
        }

        private void tbxNome_Enter(object sender, EventArgs e)
        {
            if (sender is TextBox) ((TextBox)sender).SelectAll();
        }

        private void tbxSenha_Enter(object sender, EventArgs e)
        {
            if (sender is TextBox) ((TextBox)sender).SelectAll();
        }

        private void tbxNome_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyValue == 13) tbxSenha.Focus();
        }

        private void tbxSenha_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyValue == 13)
            {
                btLogin.Focus();
                btLogin.PerformClick();
            }
        }

        private void FrmLogin_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyValue == 27) this.Close();
        }
    }
}
