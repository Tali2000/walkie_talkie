using System.Threading;

namespace WalkieTalkieServer
{
    class Program
    {
        public static Server Server = new Server();

        static void Main(string[] args)
        {
            Thread.Sleep(Timeout.Infinite);
        }
    }
}
