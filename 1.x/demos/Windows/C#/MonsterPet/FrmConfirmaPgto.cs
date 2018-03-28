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
    public partial class FrmConfirmaPgto : Form
    {
        public FrmConfirmaPgto()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Dispose();
            this.DialogResult = DialogResult.OK;
        }

        private void FrmConfirmaPgto_Load(object sender, EventArgs e)
        {
            pictureBox1.Left = (this.Width - pictureBox1.Width) / 2;
            pictureBox2.Left = (this.Width - pictureBox2.Width) / 2;
            button1.Left = (this.Width - button1.Width) / 2;

            if (FrmCompras.ConfirmaPagamento != 0)
            {
                pictureBox1.Visible = false;
                pictureBox2.Visible = true;
            }
            else
            {
                pictureBox1.Visible = true;
                pictureBox2.Visible = false;
            }
        }
    }
}
