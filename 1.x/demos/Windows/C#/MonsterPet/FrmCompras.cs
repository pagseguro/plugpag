using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MonsterPet
{
    public partial class FrmCompras : Form
    {
        public static _UserLogin userslog = new _UserLogin();
        public _Item[] items = new _Item[4];
        public static int tipoPagamento;
        public static int tipoPagamentoSelecionado;
        public static Size sizePrincipal;
        public static Point location;
        public static int ConfirmaPagamento;
        public static int vlTotal;

        public FrmCompras()
        {
            InitializeComponent();
        }

        private void FrmCompras_Load(object sender, EventArgs e)
        {
            Login();

            sizePrincipal = new Size(this.Width,this.Height);
            location = new Point(this.Left, this.Top);
        }

        private void CarregaListaItems()
        {
            items[0] = new _Item();
            items[0].vlUnitario = 265;
            items[0].qtdPedido = 1;
            items[0].strVlUnitario = "R$ 2,65";

            items[1] = new _Item();
            items[1].vlUnitario = 0;
            items[1].qtdPedido = 0;
            items[1].strVlUnitario = "R$ 0,00";

            items[2] = new _Item();
            items[2].vlUnitario = 0;
            items[2].qtdPedido = 0;
            items[2].strVlUnitario = "R$ 0,00";

            items[3] = new _Item();
            items[3].vlUnitario = 0;
            items[3].qtdPedido = 0;
            items[3].strVlUnitario = "R$ 0,00";
            

            lbV_U_1.Text = items[0].strVlUnitario;
            lbV_U_2.Text = items[1].strVlUnitario;
            lbV_U_3.Text = items[2].strVlUnitario;
            lbV_U_4.Text = items[3].strVlUnitario;

            lbV_T_1.Text = "R$ 0,00";
            lbV_T_2.Text = "R$ 0,00";
            lbV_T_3.Text = "R$ 0,00";
            lbV_T_4.Text = "R$ 0,00";
            lbTotal.Text = "R$ 0,00";

            lbProd01.Text = "1";
            lbProd02.Text = "0";
            lbProd03.Text = "0";
            lbProd04.Text = "0";

            {
                int v = Convert.ToInt16(lbProd01.Text);
                lbProd01.Text = v.ToString();
                items[0].qtdPedido = v;
                lbV_T_1.Text = "R$ " + calcItem(items[0].vlUnitario, items[0].qtdPedido);
            }
        }

        private void FrmCompras_Shown(object sender, EventArgs e)
        {
            try
            {
                lbVersion.Text = "PagSeguro MonsterPet";
                lbVersion.Text += " - " + Assembly.GetEntryAssembly().GetName().Version.ToString();
            }
            catch (Exception ex) { }
        }
        
        private void btFinalizaCompra_Click(object sender, EventArgs e)
        {
            try
            {
                tipoPagamentoSelecionado = 0;
                tipoPagamento = 0;
                ConfirmaPagamento = 0;

                FrmSelPagamento_Black f = new FrmSelPagamento_Black();
                f.Width = this.Width;
                f.Height = this.Height;
                f.Left = this.Left;
                f.Top = this.Top;
                f.Opacity = 0.60;
                f.Show(this);
                f.Dispose();
                while (f.IsDisposed == false) { Application.DoEvents(); }
                Application.DoEvents();

                while (tipoPagamentoSelecionado == 0)
                {
                    Application.DoEvents();
                }

                FrmPagamentoBlack f2 = new FrmPagamentoBlack();
                f2.Width = this.Width;
                f2.Height = this.Height;
                f2.Left = this.Left;
                f2.Top = this.Top;
                f2.Opacity = 0.60;
                ConfirmaPagamento = -1;
                f2.Show(this);
                f2.Dispose();
                while (f2.IsDisposed == false) { Application.DoEvents(); }
                Application.DoEvents();

                while (ConfirmaPagamento == -1)
                {
                    Application.DoEvents();
                }

                FrmConfirmaPgto f3 = new FrmConfirmaPgto();
                f3.Left = FrmCompras.location.X + pnlBaseCompra.Left;
                f3.Top = pnlBaseCompra.Top + 101;
                f3.Width = pnlBaseCompra.Width;
                f3.Height = pnlBaseCompra.Height-101;
                f3.ShowDialog();
                f3.Dispose();
                while (f3.IsDisposed == false) { Application.DoEvents(); }
                Application.DoEvents();

                CarregaListaItems();

                Application.DoEvents();
            }
            catch { }
        }

        private void lbSair_Click(object sender, EventArgs e)
        {
            //   Login();
            Application.Exit();
        }

        private void Login()
        {
            try
            {
                userslog.Password = "PAGSEGURO";
                userslog.LoginStatus = DialogResult.Cancel;


             //   System.Threading.Thread.Sleep(300);
               // FrmLogin frmLogin = new FrmLogin();
                this.Hide();

               // while (frmLogin.ShowDialog() != DialogResult.OK) { Application.DoEvents(); }

              //  if (userslog.LoginStatus != DialogResult.OK) Application.Exit();


                this.SuspendLayout();
                this.BackColor = Color.FromArgb(215, 215, 215);
                pnlBaseCompra.SuspendLayout();
                pnlBaseCompra.Left = (this.Width - pnlBaseCompra.Width) / 2;
                pnlBaseCompra.Top = (this.Height - pnlBaseCompra.Height) / 2;

             //   pnlUpDw1.BringToFront();

                pnlBaseCompra.SendToBack();

                lbUser.Text = userslog.UserName;
                lbUser.Left = 1046 - lbUser.Width;
                lbUserOla.Left = lbUser.Left - 42;
                lbSair.ForeColor = Color.FromArgb(16, 130, 190);

                CarregaListaItems();

                pnlBaseCompra.ResumeLayout(false);
                this.ResumeLayout(true);

                this.Show();
            }
            catch (Exception ex) { }
            //******************************************************************************************************
        }

        private string calcItem(int vlUnit, int qtd)
        {
            int vlTotal_Inteiro = (vlUnit * qtd) / 100;
            int vlTotal_fracao = (vlUnit * qtd) % 100;

            calcTotal();
            return vlTotal_Inteiro.ToString() + "," + vlTotal_fracao.ToString("00");
        }

        private void calcTotal()
        {
            int vlTotal = 0;

            vlTotal =  items[0].vlUnitario * items[0].qtdPedido;
            vlTotal += items[1].vlUnitario * items[1].qtdPedido;
            vlTotal += items[2].vlUnitario * items[2].qtdPedido;
            vlTotal += items[3].vlUnitario * items[3].qtdPedido;

            FrmCompras.vlTotal = vlTotal;

            int vlTotal_Inteiro = (vlTotal) / 100;
            int vlTotal_fracao = (vlTotal) % 100;

            lbTotal.Text = "R$ " + vlTotal_Inteiro.ToString() + "," + vlTotal_fracao.ToString("00");
        }

        private void bt1m_Click(object sender, EventArgs e)
        {
            // - lbProd01
            int v = Convert.ToInt16(lbProd01.Text);
            if (v > 0) v--;
            lbProd01.Text = v.ToString();
            items[0].qtdPedido = v;
            lbV_T_1.Text = "R$ " + calcItem(items[0].vlUnitario, items[0].qtdPedido);
        }

        private void bt1M__Click(object sender, EventArgs e)
        {
            // + lbProd01
            int v = Convert.ToInt16(lbProd01.Text);
            if (v < 99) v++;
            lbProd01.Text = v.ToString();
            items[0].qtdPedido = v;
            lbV_T_1.Text = "R$ " + calcItem(items[0].vlUnitario, items[0].qtdPedido);
        }

        private void bt2m_Click(object sender, EventArgs e)
        {
            // - lbProd02
            int v = Convert.ToInt16(lbProd02.Text);
            if (v > 0) v--;
            lbProd02.Text = v.ToString();
            items[1].qtdPedido = v;
            lbV_T_2.Text = "R$ " + calcItem(items[1].vlUnitario, items[1].qtdPedido);
        }

        private void bt2M__Click(object sender, EventArgs e)
        {
            // + lbProd02
            int v = Convert.ToInt16(lbProd02.Text);
            if (v < 99) v++;
            lbProd02.Text = v.ToString();
            items[1].qtdPedido = v;
            lbV_T_2.Text = "R$ " + calcItem(items[1].vlUnitario, items[1].qtdPedido);
        }

        private void bt3m_Click(object sender, EventArgs e)
        {
            // - lbProd03
            int v = Convert.ToInt16(lbProd03.Text);
            if (v > 0) v--;
            lbProd03.Text = v.ToString();
            items[2].qtdPedido = v;
            lbV_T_3.Text = "R$ " + calcItem(items[2].vlUnitario, items[2].qtdPedido);
        }

        private void bt3M__Click(object sender, EventArgs e)
        {
            // + lbProd03
            int v = Convert.ToInt16(lbProd03.Text);
            if (v < 99) v++;
            lbProd03.Text = v.ToString();
            items[2].qtdPedido = v;
            lbV_T_3.Text = "R$ " + calcItem(items[2].vlUnitario, items[2].qtdPedido);
        }

        private void bt4m_Click(object sender, EventArgs e)
        {
            // - lbProd04
            int v = Convert.ToInt16(lbProd04.Text);
            if (v > 0) v--;
            lbProd04.Text = v.ToString();
            items[3].qtdPedido = v;
            lbV_T_4.Text = "R$ " + calcItem(items[3].vlUnitario, items[3].qtdPedido);
        }

        private void bt4M__Click(object sender, EventArgs e)
        {
            // + lbProd04
            int v = Convert.ToInt16(lbProd04.Text);
            if (v < 99) v++;
            lbProd04.Text = v.ToString();
            items[3].qtdPedido = v;
            lbV_T_4.Text = "R$ " + calcItem(items[3].vlUnitario, items[3].qtdPedido);
        }

        private void FrmCompras_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyValue == 27) this.Close();
        }

        private void lbProd01_Click(object sender, EventArgs e)
        {

        }

        private void btEstornaCompra_Click(object sender, EventArgs e)
        {
            tipoPagamentoSelecionado = 1;
  

            FrmPagamentoBlack f2 = new FrmPagamentoBlack();

            FrmCompras.tipoPagamento = 0;

            f2.Width = this.Width;
            f2.Height = this.Height;
            f2.Left = this.Left;
            f2.Top = this.Top;
            f2.Opacity = 0.60;
            ConfirmaPagamento = -1;
            f2.Show(this);
            f2.Dispose();
            while (f2.IsDisposed == false) { Application.DoEvents(); }
            Application.DoEvents();

            while (ConfirmaPagamento == -1)
            {
                Application.DoEvents();
            }

            FrmConfirmaPgto f3 = new FrmConfirmaPgto();
            f3.Left = FrmCompras.location.X + pnlBaseCompra.Left;
            f3.Top = pnlBaseCompra.Top + 101;
            f3.Width = pnlBaseCompra.Width;
            f3.Height = pnlBaseCompra.Height - 101;
            f3.ShowDialog();
            f3.Dispose();
            while (f3.IsDisposed == false) { Application.DoEvents(); }
            Application.DoEvents();

            CarregaListaItems();

            Application.DoEvents();
        }
    }
}
