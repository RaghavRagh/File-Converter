import "./App.css";
import Background  from "./componenet/Background";
import Foreground from "./componenet/Foreground";

function App() {
  return (
    <div className="relative w-full h-screen bg-zinc-800">
      <Background />
      <Foreground />
    </div>
  );
}

export default App;
