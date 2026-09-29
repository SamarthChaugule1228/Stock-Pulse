export default function Button({ children, ...props }) {
  return <button className={`button ${props.className || ''}`} {...props}>{children}</button>;
}
