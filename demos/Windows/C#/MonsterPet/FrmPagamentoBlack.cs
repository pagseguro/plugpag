using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MonsterPet
{
    public partial class FrmPagamentoBlack : Form
    {
        public FrmPagamentoBlack()
        {
            InitializeComponent();
        }

        private void FrmPagamentoBlack_Activated(object sender, EventArgs e)
        {
            if (FrmCompras.ConfirmaPagamento == -1)
            {
                try
                {
                    FrmPagamento f = new FrmPagamento();
                    f.Left = FrmCompras.location.X + (FrmCompras.sizePrincipal.Width - 539) / 2;
                    f.Top = (FrmCompras.sizePrincipal.Height - 349) / 2;

                    f.ShowDialog(this);
                    f.Dispose();
                    while (f.IsDisposed == false) { Application.DoEvents(); }
                }
                catch { }
            }
            this.Close();
        }
    }
}
