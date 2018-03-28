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
    public partial class FrmSelPagamento_Black : Form
    {
        public FrmSelPagamento_Black()
        {
            InitializeComponent();
        }

        private void FrmSelPagamento_Activated(object sender, EventArgs e)
        {
            if (FrmCompras.tipoPagamento == 0)
            {
                FrmSelPagamento f = new FrmSelPagamento();
                f.Left = FrmCompras.location.X + (FrmCompras.sizePrincipal.Width - 815) / 2;
                f.Top = (FrmCompras.sizePrincipal.Height - 413) / 2;

                try
                {
                    f.ShowDialog(this);
                    f.Dispose();
                }
                catch { }

                this.Close();
            }
        }
    }
}
