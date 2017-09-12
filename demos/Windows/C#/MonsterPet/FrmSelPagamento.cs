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
    public partial class FrmSelPagamento : Form
    {
        public FrmSelPagamento()
        {
            InitializeComponent();
        }

        private void btCredito_Click(object sender, EventArgs e)
        {
            FrmCompras.tipoPagamento = 1;
            btCredito.Image = global::MonsterPet.Properties.Resources.creditoOn;
            btDebito.Image = global::MonsterPet.Properties.Resources.debitoOff;
        }

        private void btDebito_Click(object sender, EventArgs e)
        {
            FrmCompras.tipoPagamento = 2;
            btCredito.Image = global::MonsterPet.Properties.Resources.creditoOff;
            btDebito.Image = global::MonsterPet.Properties.Resources.debitoOn;
        }

        private void btAvancar_Click(object sender, EventArgs e)
        {
            FrmCompras.tipoPagamentoSelecionado = 1;
            this.Dispose();
            while (this.IsDisposed == false) { Application.DoEvents(); }
            this.DialogResult = DialogResult.OK;
        }

        private void FrmSelPagamento_Shown(object sender, EventArgs e)
        {
            FrmCompras.tipoPagamento = 1;
        }
    }
}
